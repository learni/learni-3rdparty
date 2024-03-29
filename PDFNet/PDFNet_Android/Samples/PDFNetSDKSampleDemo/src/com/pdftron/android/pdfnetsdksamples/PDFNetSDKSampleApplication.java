//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package com.pdftron.android.pdfnetsdksamples;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;

import com.pdftron.android.pdfnetsdksamples.samples.AddImageSample;
import com.pdftron.android.pdfnetsdksamples.samples.AnnotationSample;
import com.pdftron.android.pdfnetsdksamples.samples.BookmarksSample;
import com.pdftron.android.pdfnetsdksamples.samples.ContentReplacerSample;
import com.pdftron.android.pdfnetsdksamples.samples.DigitalSignaturesSample;
import com.pdftron.android.pdfnetsdksamples.samples.EditTextSample;
import com.pdftron.android.pdfnetsdksamples.samples.ElementBuilderSample;
import com.pdftron.android.pdfnetsdksamples.samples.ElementEditSample;
import com.pdftron.android.pdfnetsdksamples.samples.ElementReaderAdvSample;
import com.pdftron.android.pdfnetsdksamples.samples.ElementReaderSample;
import com.pdftron.android.pdfnetsdksamples.samples.EncryptionSample;
import com.pdftron.android.pdfnetsdksamples.samples.FDFSample;
import com.pdftron.android.pdfnetsdksamples.samples.ImageExtractSample;
import com.pdftron.android.pdfnetsdksamples.samples.ImpositionSample;
import com.pdftron.android.pdfnetsdksamples.samples.InteractiveFormsSample;
import com.pdftron.android.pdfnetsdksamples.samples.JBIG2Sample;
import com.pdftron.android.pdfnetsdksamples.samples.LogicalStructureSample;
import com.pdftron.android.pdfnetsdksamples.samples.OptimizerSample;
import com.pdftron.android.pdfnetsdksamples.samples.PDFASample;
import com.pdftron.android.pdfnetsdksamples.samples.PDFDocMemorySample;
import com.pdftron.android.pdfnetsdksamples.samples.PDFDrawSample;
import com.pdftron.android.pdfnetsdksamples.samples.PDFLayersSample;
import com.pdftron.android.pdfnetsdksamples.samples.PDFPackageSample;
import com.pdftron.android.pdfnetsdksamples.samples.PDFPageSample;
import com.pdftron.android.pdfnetsdksamples.samples.PDFRedactSample;
import com.pdftron.android.pdfnetsdksamples.samples.PageLabelsSample;
import com.pdftron.android.pdfnetsdksamples.samples.PatternSample;
import com.pdftron.android.pdfnetsdksamples.samples.RectSample;
import com.pdftron.android.pdfnetsdksamples.samples.SDFSample;
import com.pdftron.android.pdfnetsdksamples.samples.StamperSample;
import com.pdftron.android.pdfnetsdksamples.samples.TextExtractSample;
import com.pdftron.android.pdfnetsdksamples.samples.TextSearchSample;
import com.pdftron.android.pdfnetsdksamples.samples.U3DSample;
import com.pdftron.android.pdfnetsdksamples.samples.UnicodeWriteSample;

public class PDFNetSDKSampleApplication extends Application {

    private List<PDFNetSample> mListSamples = new ArrayList<PDFNetSample>();
    private static PDFNetSDKSampleApplication singleton;

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        
        mListSamples.add(new AnnotationSample());
        mListSamples.add(new AddImageSample());
        mListSamples.add(new ContentReplacerSample());
        mListSamples.add(new BookmarksSample());
        mListSamples.add(new DigitalSignaturesSample());
        mListSamples.add(new EncryptionSample());
        mListSamples.add(new ElementBuilderSample());
        mListSamples.add(new EditTextSample());
        mListSamples.add(new ElementEditSample());
        mListSamples.add(new ElementReaderSample());
        mListSamples.add(new ElementReaderAdvSample());
        mListSamples.add(new FDFSample());
        mListSamples.add(new ImageExtractSample());
        mListSamples.add(new ImpositionSample());
        mListSamples.add(new InteractiveFormsSample());
        mListSamples.add(new JBIG2Sample());
        mListSamples.add(new LogicalStructureSample());
        mListSamples.add(new PageLabelsSample());
        mListSamples.add(new PatternSample());
        mListSamples.add(new PDFASample());
        mListSamples.add(new PDFDrawSample());
        mListSamples.add(new PDFLayersSample());
        mListSamples.add(new PDFDocMemorySample());
        mListSamples.add(new PDFPackageSample());
        mListSamples.add(new PDFPageSample());
        mListSamples.add(new PDFRedactSample());
        mListSamples.add(new RectSample());
        mListSamples.add(new SDFSample());
        mListSamples.add(new StamperSample());
        mListSamples.add(new OptimizerSample());
        mListSamples.add(new TextExtractSample());
        mListSamples.add(new TextSearchSample());
        mListSamples.add(new U3DSample());
        mListSamples.add(new UnicodeWriteSample());

    }

    public List<PDFNetSample> getContent() {
        return this.mListSamples;
    }
    
    public static PDFNetSDKSampleApplication getInstance() {
        return singleton;
    }
}
