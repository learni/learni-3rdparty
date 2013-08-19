//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package com.pdftron.android.pdfnetsdksamples.samples;

import pdftron.PDF.ColorPt;
import pdftron.PDF.ColorSpace;
import pdftron.PDF.Element;
import pdftron.PDF.ElementReader;
import pdftron.PDF.ElementWriter;
import pdftron.PDF.GState;
import pdftron.PDF.PDFDoc;
import pdftron.PDF.Page;
import pdftron.PDF.PageIterator;
import pdftron.SDF.SDFDoc;

import com.pdftron.android.pdfnetsdksamples.OutputListener;
import com.pdftron.android.pdfnetsdksamples.PDFNetSample;
import com.pdftron.android.pdfnetsdksamples.R;
import com.pdftron.android.pdfnetsdksamples.util.Utils;

public class EditTextSample extends PDFNetSample {

    public EditTextSample() {
        setTitle(R.string.sample_edittext_title);
        setDescription(R.string.sample_edittext_description);
    }
    
    @Override
    public void run(OutputListener outputListener) {
        super.run(outputListener);
        printHeader(outputListener);

        try {
            PDFDoc doc = new PDFDoc(Utils.getAssetInputStream(INPUT_PATH + "newsletter.pdf"));
            doc.initSecurityHandler();

            int num_pages = doc.getPageCount();

            ElementWriter writer = new ElementWriter();
            ElementReader reader = new ElementReader();
            Element element;

            for (int i = 1; i <= num_pages; ++i) {
                PageIterator itr = doc.getPageIterator(i);
                Page page = (Page) (itr.next());
                reader.begin(page);

                Page new_page = doc.pageCreate();
                PageIterator next_page = itr;
                doc.pageInsert(next_page, new_page);

                writer.begin(new_page);
                while ((element = reader.next()) != null) { // Read page contents
                    if (element.getType() == Element.e_text) {
                        // Set all text to blue color.
                        GState gs = element.getGState();
                        gs.setFillColorSpace(ColorSpace.createDeviceRGB());
                        gs.setFillColor(new ColorPt(0, 0, 1));

                        byte[] text_arr = element.getTextData();
                        int size = text_arr.length;

                        byte[] text_arr_new = new byte[size];

                        boolean bUnicode = false;
                        if (size > 2) {
                            if (text_arr[0] == 254 && text_arr[1] == 255) {
                                bUnicode = true;
                                for (int n = 0; n < size; n++) {
                                    // copy Unicode string as is
                                    text_arr_new[n] = text_arr[n];
                                }
                            }
                        }
                        if (!bUnicode) {
                            for (int n = 0; n < size; n++) {
                                // replace 'c' with 'k' and 'k' with 'c'
                                switch (text_arr[n]) {
                                case 99:
                                    text_arr_new[n] = 107;
                                    break;
                                case 107:
                                    text_arr_new[n] = 99;
                                    break;
                                default:
                                    text_arr_new[n] = text_arr[n];
                                }
                            }
                        }
                        element.setTextData(text_arr_new);
                        writer.writeElement(element);
                    } else if (element.getType() == Element.e_image) {
                        // remove all images
                        continue;
                    } else {
                        writer.writeElement(element);
                    }
                }

                writer.end();
                reader.end();
                new_page.setMediaBox(page.getCropBox());
                doc.pageRemove(doc.getPageIterator(i));
            }

            doc.save(Utils.createExternalFile("newsletter_ed.pdf").getAbsolutePath(), SDFDoc.e_remove_unused, null);
            //doc.save(Utils.createExternalFile("newsletter_ed.pdf").getAbsolutePath(), SDFDoc.e_linearized, null);
            doc.close();
            addToFileList("newsletter_ed.pdf");

        } catch (Exception e) {
            outputListener.println(e.getStackTrace());
        }

        printFooter(outputListener);
    }

}
