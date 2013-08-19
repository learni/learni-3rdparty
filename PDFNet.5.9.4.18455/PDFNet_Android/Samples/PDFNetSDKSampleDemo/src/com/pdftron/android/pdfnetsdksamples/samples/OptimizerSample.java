//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package com.pdftron.android.pdfnetsdksamples.samples;

import pdftron.PDF.Optimizer;
import pdftron.PDF.PDFDoc;
import pdftron.SDF.SDFDoc;

import com.pdftron.android.pdfnetsdksamples.OutputListener;
import com.pdftron.android.pdfnetsdksamples.PDFNetSample;
import com.pdftron.android.pdfnetsdksamples.R;
import com.pdftron.android.pdfnetsdksamples.util.Utils;

/**
 * The following sample illustrates how to reduce PDF file size using the
 * Optimizer class.
 * 
 * 'pdftron.PDF.Optimizer' is an optional PDFNet Add-On utility class that can be
 * used to optimize PDF documents by reducing the file size, removing redundant
 * information, and compressing data streams using the latest in image compression
 * technology. PDF Optimizer can compress and shrink PDF file size with the
 * following operations:
 * - Remove duplicated fonts, images, ICC profiles, and any other data stream.
 * - Optionally convert high-quality or print-ready PDF files to small, efficient and web-ready PDF.
 * - Optionally down-sample large images to a given resolution.
 * - Optionally compress or recompress PDF images using JBIG2 and JPEG2000 compression formats.
 * - Compress uncompressed streams and remove unused PDF objects.
 */
public class OptimizerSample extends PDFNetSample {

    public OptimizerSample() {
        setTitle(R.string.sample_optimizer_title);
        setDescription(R.string.sample_optimizer_description);
    }

    @Override
    public void run(OutputListener outputListener) {
        super.run(outputListener);
        printHeader(outputListener);
        
        String input_filename = "newsletter.pdf";
        
        //--------------------------------------------------------------------------------
        // Example 1) Optimize a PDF. 
        try {
            PDFDoc doc = new PDFDoc(Utils.getAssetInputStream(INPUT_PATH + input_filename));
            doc.initSecurityHandler();
            Optimizer.optimize(doc);
            doc.save(Utils.createExternalFile(input_filename + "_opt1.pdf").getAbsolutePath(),
                    SDFDoc.e_linearized, null);
            addToFileList(input_filename + "_opt1.pdf");
        } catch (Exception e) {
            outputListener.println(e.getStackTrace());
        }

        //--------------------------------------------------------------------------------
        // Example 2) Reduce image quality and use jpeg compression for
        // non monochrome images.
        try {
            PDFDoc doc = new PDFDoc(Utils.getAssetInputStream(INPUT_PATH + input_filename));
            doc.initSecurityHandler();

            Optimizer.ImageSettings image_settings = new Optimizer.ImageSettings();

            // low quality jpeg compression
            image_settings.setCompressionMode(Optimizer.ImageSettings.e_jpeg);
            image_settings.setQuality(1);

            // Set the output dpi to be standard screen resolution
            image_settings.setImageDPI(144, 96);

            // this option will recompress images not compressed with
            // jpeg compression and use the result if the new image
            // is smaller.
            image_settings.forceRecompression(true);

            // this option is not commonly used since it can
            // potentially lead to larger files. It should be enabled
            // only if the output compression specified should be applied
            // to every image of a given type regardless of the output image
            // size
            // image_settings.forceChanges(true);

            Optimizer.OptimizerSettings opt_settings = new Optimizer.OptimizerSettings();
            opt_settings.setColorImageSettings(image_settings);
            opt_settings.setGrayscaleImageSettings(image_settings);

            Optimizer.optimize(doc, opt_settings);

            doc.save(Utils.createExternalFile(input_filename + "_opt2.pdf").getAbsolutePath(),
                    SDFDoc.e_linearized, null);
            addToFileList(input_filename + "_opt2.pdf");
        } catch (Exception e) {
            outputListener.println(e.getStackTrace());
        }
        
        //--------------------------------------------------------------------------------
        // Example 3) Use monochrome image settings and default settings
        // for color and grayscale images. 
        try {
            PDFDoc doc = new PDFDoc(Utils.getAssetInputStream(INPUT_PATH + input_filename));
            doc.initSecurityHandler();

            Optimizer.MonoImageSettings mono_image_settings = new Optimizer.MonoImageSettings();
            mono_image_settings.setCompressionMode(Optimizer.MonoImageSettings.e_jbig2);
            mono_image_settings.forceRecompression(true);
            Optimizer.OptimizerSettings opt_settings = new Optimizer.OptimizerSettings();
            opt_settings.setMonoImageSettings(mono_image_settings);

            Optimizer.optimize(doc, opt_settings);

            doc.save(Utils.createExternalFile(input_filename + "_opt3.pdf").getAbsolutePath(),
                    SDFDoc.e_linearized, null);
            addToFileList(input_filename + "_opt3.pdf");
        } catch (Exception e) {
            outputListener.println(e.getStackTrace());
        }
        
        printFooter(outputListener);
    }

}
