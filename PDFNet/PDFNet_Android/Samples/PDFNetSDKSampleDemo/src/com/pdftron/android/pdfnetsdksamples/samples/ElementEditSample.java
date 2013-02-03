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

public class ElementEditSample extends PDFNetSample {

    private static OutputListener mOutputListener;
    
    public ElementEditSample() {
        setTitle(R.string.sample_elementedit_title);
        setDescription(R.string.sample_elementedit_description);
    }
    
    @Override
    public void run(OutputListener outputListener) {
        super.run(outputListener);
        mOutputListener = outputListener;

        printHeader(outputListener);

        String input_filename = "newsletter.pdf";
        String output_filename = "newsletter_edited.pdf";

        try {
            PDFDoc doc = new PDFDoc(Utils.getAssetInputStream(INPUT_PATH + input_filename));
            doc.initSecurityHandler();

            ElementWriter writer = new ElementWriter();
            ElementReader reader = new ElementReader();

            PageIterator itr = doc.getPageIterator();

            while (itr.hasNext()) {
                Page page = (Page) itr.next();
                reader.begin(page);
                writer.begin(page, ElementWriter.e_replacement, false);
                processElements(writer, reader);
                writer.end();
                reader.end();
            }

            doc.save(Utils.createExternalFile(output_filename).getAbsolutePath(), SDFDoc.e_remove_unused, null);
            doc.close();
            outputListener.println("Result saved in " + output_filename + ".");
            addToFileList(output_filename);
        } catch (Exception e) {
            outputListener.println(e.getStackTrace());
        }

        printFooter(outputListener);
    }
    
    public static void processElements(ElementWriter writer, ElementReader reader)
    {
        Element element;
        try {
            while ((element = reader.next()) != null) {
                switch (element.getType()) {
                    case Element.e_path:
                        break;
                    case Element.e_image:
                    case Element.e_inline_image:
                        continue;
                    case Element.e_text:
                        GState gs = element.getGState();
                        gs.setFillColorSpace(ColorSpace.createDeviceRGB());
                        gs.setFillColor(new ColorPt(0, 0, 1));
                        break;
                    case Element.e_form:
                        reader.formBegin();
                        processElements(writer, reader);
                        reader.end();
                    break;
                }
                writer.writeElement(element);
            }
        } catch (Exception e) {
            mOutputListener.println(e.getStackTrace());
        }
    }
}
