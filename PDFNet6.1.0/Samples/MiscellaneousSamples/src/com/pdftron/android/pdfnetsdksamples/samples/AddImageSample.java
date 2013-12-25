//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package com.pdftron.android.pdfnetsdksamples.samples;

import pdftron.Common.Matrix2D;
import pdftron.Common.PDFNetException;
import pdftron.PDF.Element;
import pdftron.PDF.ElementBuilder;
import pdftron.PDF.ElementWriter;
import pdftron.PDF.Font;
import pdftron.PDF.Image;
import pdftron.PDF.PDFDoc;
import pdftron.PDF.Page;
import pdftron.SDF.SDFDoc;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.pdftron.android.pdfnetsdksamples.OutputListener;
import com.pdftron.android.pdfnetsdksamples.PDFNetSample;
import com.pdftron.android.pdfnetsdksamples.R;
import com.pdftron.android.pdfnetsdksamples.util.Utils;

public class AddImageSample extends PDFNetSample {
    
    public AddImageSample() {
        setTitle(R.string.sample_addimage_title);
        setDescription(R.string.sample_addimage_description);
    }
    
    @Override
    public void run(OutputListener outputListener) {
        super.run(outputListener);
        printHeader(outputListener);
        
        try {
            PDFDoc doc = new PDFDoc();

            ElementBuilder f = new ElementBuilder(); // Used to build new Element objects
            ElementWriter writer = new ElementWriter(); // Used to write Elements to the page

            Page page = doc.pageCreate(); // Start a new page
            writer.begin(page); // Begin writing to this page

            // ----------------------------------------------------------
            // Add JPEG image to the output file
            Image img = Image.create(doc.getSDFDoc(), Utils.getAssetTempFile(INPUT_PATH + "peppers.jpg").getAbsolutePath());
            Element element = f.createImage(img, new Matrix2D(200, 0, 0, 250, 50, 500));
            writer.writePlacedElement(element);

            // ----------------------------------------------------------
            // Add a PNG image to the output file
            Bitmap bitmap = BitmapFactory.decodeStream(Utils.getAssetInputStream(INPUT_PATH + "butterfly.png"));
            img = Image.create(doc.getSDFDoc(), bitmap);
            element = f.createImage(img, new Matrix2D(img.getImageWidth(), 0, 0, img.getImageHeight(), 300, 500));
            writer.writePlacedElement(element);

            // ----------------------------------------------------------
            // Add a GIF image to the output file
            bitmap = BitmapFactory.decodeStream(Utils.getAssetInputStream(INPUT_PATH + "pdfnet.gif"));
            img = Image.create(doc.getSDFDoc(), bitmap);
            element = f.createImage(img, new Matrix2D(img.getImageWidth(), 0, 0, img.getImageHeight(), 50, 350));
            writer.writePlacedElement(element);

            // ----------------------------------------------------------
            // Add a TIFF image to the output file

            /*
             * img = Image.create(doc.getSDFDoc(), (input_path +
             * "grayscale.tif")); element = f.createImage(img, new
             * Matrix2D(img.getImageWidth(), 0, 0, img.getImageHeight(), 10,
             * 50)); writer.writePlacedElement(element);
             * 
             * writer.end(); // Save the page doc.pagePushBack(page); // Add the
             * page to the document page sequence
             * 
             * // ---------------------------------------------------------- //
             * Embed a monochrome TIFF. Compress the image using lossy JBIG2
             * filter.
             * 
             * page = doc.pageCreate(new Rect(0, 0, 612, 794));
             * writer.begin(page); // begin writing to this page
             * 
             * // Note: encoder hints can be used to select between different
             * compression methods. // For example to instruct PDFNet to
             * compress a monochrome image using JBIG2 compression. //ObjSet
             * hint_set=new ObjSet(); //Obj enc=hint_set.createArray(); //
             * Initilaize encoder 'hint' parameter //enc.PushBackName("JBIG2");
             * //enc.PushBackName("Lossy");
             * 
             * img = Image.create(doc.getSDFDoc(), (input_path +
             * "multipage.tif")); element = f.createImage(img, new Matrix2D(612,
             * 0, 0, 794, 0, 0)); writer.writePlacedElement(element);
             */

            writer.end(); // Save the page
            doc.pagePushBack(page); // Add the page to the document page sequence

            // ----------------------------------------------------------
            // Add a JPEG2000 (JP2) image to the output file
            // Create a new page
            page = doc.pageCreate();
            writer.begin(page); // Begin writing to the page

            // Embed the image.
            img = Image.create(doc.getSDFDoc(), Utils.getAssetTempFile(INPUT_PATH + "palm.jp2").getAbsolutePath());

            // Position the image on the page.
            element = f.createImage(img, new Matrix2D(img.getImageWidth(), 0, 0, img.getImageHeight(), 96, 80));
            writer.writePlacedElement(element);

            // Write 'JPEG2000 Sample' text string under the image.
            writer.writeElement(f.createTextBegin(Font.create(doc.getSDFDoc(), Font.e_times_roman), 32));
            element = f.createTextRun("JPEG2000 Sample");
            element.setTextMatrix(1, 0, 0, 1, 190, 30);
            writer.writeElement(element);
            writer.writeElement(f.createTextEnd());

            writer.end(); // Finish writing to the page
            doc.pagePushBack(page);

            doc.save(Utils.createExternalFile("addimage.pdf").getAbsolutePath(),SDFDoc.e_linearized, null);
            addToFileList("addimage.pdf");

            doc.close();

            printFooter(outputListener);
            
        } catch (PDFNetException e) {
            outputListener.println(e.getStackTrace());
        } catch (Exception e) {
            outputListener.println(e.getStackTrace());
        }
    }
}
