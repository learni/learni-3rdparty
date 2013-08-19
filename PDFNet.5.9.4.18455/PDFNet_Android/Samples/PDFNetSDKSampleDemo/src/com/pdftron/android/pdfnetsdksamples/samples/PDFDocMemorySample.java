//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package com.pdftron.android.pdfnetsdksamples.samples;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import pdftron.Common.PDFNetException;
import pdftron.Filters.FilterReader;
import pdftron.Filters.FilterWriter;
import pdftron.Filters.StdFile;
import pdftron.PDF.Element;
import pdftron.PDF.ElementReader;
import pdftron.PDF.ElementWriter;
import pdftron.PDF.PDFDoc;
import pdftron.PDF.Page;
import pdftron.PDF.PageIterator;
import pdftron.SDF.SDFDoc;

import com.pdftron.android.pdfnetsdksamples.OutputListener;
import com.pdftron.android.pdfnetsdksamples.PDFNetSample;
import com.pdftron.android.pdfnetsdksamples.R;
import com.pdftron.android.pdfnetsdksamples.util.Utils;

public class PDFDocMemorySample extends PDFNetSample {

    public PDFDocMemorySample() {
        setTitle(R.string.sample_pdfdocmemory_title);
        setDescription(R.string.sample_pdfdocmemory_description);
    }

    @Override
    public void run(OutputListener outputListener) {
        super.run(outputListener);
        printHeader(outputListener);

        // The following sample illustrates how to read/write a PDF document from/to
        // a memory buffer. This is useful for applications that work with dynamic PDF
        // documents that don't need to be saved/read from a disk.
        try {
            // Read a PDF document in a memory buffer.
            StdFile file = new StdFile(Utils.getAssetTempFile(INPUT_PATH + "tiger.pdf").getAbsolutePath(), StdFile.e_read_mode);
            long file_sz = file.fileSize();

            FilterReader file_reader = new FilterReader(file);

            byte[] mem = new byte[(int) file_sz];

            @SuppressWarnings("unused")
            long bytes_read = file_reader.read(mem);
            PDFDoc doc = new PDFDoc(mem);

            doc.initSecurityHandler();
            int num_pages = doc.getPageCount();

            ElementWriter writer = new ElementWriter();
            ElementReader reader = new ElementReader();
            Element element;

            // Create a duplicate of every page but copy only path objects

            for (int i = 1; i <= num_pages; ++i) {
                PageIterator itr = doc.getPageIterator(2 * i - 1);
                Page current = (Page) (itr.next());
                reader.begin(current);
                Page new_page = doc.pageCreate(current.getMediaBox());
                doc.pageInsert(itr, new_page);

                writer.begin(new_page);
                while ((element = reader.next()) != null) // Read page contents
                {
                    if (element.getType() == Element.e_path)
                        writer.writeElement(element);
                }

                writer.end();
                reader.end();
            }

            doc.save(Utils.createExternalFile("doc_memory_edit.pdf").getAbsolutePath(), SDFDoc.e_remove_unused, null);
            addToFileList("doc_memory_edit.pdf");

            // Save the document to a memory buffer.

            byte[] buf = doc.save(SDFDoc.e_remove_unused, null);

            // Write the contents of the buffer to the disk
            {
                StdFile outfile = new StdFile(Utils.createExternalFile("doc_memory_edit.txt").getAbsolutePath(), StdFile.e_write_mode);
                addToFileList("doc_memory_edit.txt");
                FilterWriter fwriter = new FilterWriter(outfile);
                fwriter.writeBuffer(buf);
                fwriter.flush();
            }

            // Read some data from the file stored in memory
            reader.begin(doc.getPage(1));
            while ((element = reader.next()) != null) {
                if (element.getType() == Element.e_path)
                    outputListener.print("Path, ");
            }
            reader.end();

            doc.close();
            outputListener.println("\n\nDone. Result saved in doc_memory_edit.pdf and doc_memory_edit.txt ...");
        } catch (PDFNetException e) {
            outputListener.println(e.getStackTrace());
        }

        // This sample illustrates how to open a PDF document
        // from an Java InputStream and how to save to an OutputStream.
        try {
            PDFDoc doc = new PDFDoc(Utils.getAssetInputStream(INPUT_PATH + "newsletter.pdf"));
            doc.save(new FileOutputStream(Utils.getExternalFilesDirPath() + "/StreamTest.pdf"), 0, null);
            addToFileList("StreamTest.pdf");
            doc.close();
            outputListener.println("Done. Result saved in StreamTest.pdf ...");
        } catch (PDFNetException e) {
            outputListener.println(e.getStackTrace());
        } catch (FileNotFoundException e) {
            outputListener.println(e.getStackTrace());
        } catch (IOException e) {
            outputListener.println(e.getStackTrace());
        }

        printFooter(outputListener);
    }

}
