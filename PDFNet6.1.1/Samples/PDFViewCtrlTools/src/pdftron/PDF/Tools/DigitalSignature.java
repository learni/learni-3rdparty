//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package pdftron.PDF.Tools;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.spongycastle.cert.jcajce.JcaCertStore;
import org.spongycastle.cms.CMSProcessableByteArray;
import org.spongycastle.cms.CMSSignedData;
import org.spongycastle.cms.CMSSignedDataGenerator;
import org.spongycastle.cms.CMSTypedData;
import org.spongycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.operator.ContentSigner;
import org.spongycastle.operator.jcajce.JcaContentSignerBuilder;
import org.spongycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.spongycastle.util.Store;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.TreeSet;

import pdftron.Common.Matrix2D;
import pdftron.Common.PDFNetException;
import pdftron.PDF.Annot;
import pdftron.PDF.Annots.Widget;
import pdftron.PDF.ColorPt;
import pdftron.PDF.ColorSpace;
import pdftron.PDF.Element;
import pdftron.PDF.ElementBuilder;
import pdftron.PDF.ElementReader;
import pdftron.PDF.ElementWriter;
import pdftron.PDF.Field;
import pdftron.PDF.GState;
import pdftron.PDF.Image;
import pdftron.PDF.PDFDoc;
import pdftron.PDF.PDFViewCtrl;
import pdftron.PDF.Page;
import pdftron.PDF.Rect;
import pdftron.SDF.Obj;
import pdftron.SDF.SDFDoc;
import pdftron.SDF.SignatureHandler;
import com.pdftron.pdf.tools.R;

/**
 * The purpose of this tool is to demonstrate how to digitally sign a document by using one of
 * its signature fields.
 * <p>
 * The sign process normally takes these steps:
 * <ul>
 * <li>User taps a signature field to add a signature</li>
 * <li>Here we give the user two options: use an image as the signature appearance or the user
 * can draw the signature (in this case we create an appearance with paths)</li>
 * <li>Finally the user digitally signs the document using a certificate</li>
 * </ul>
 * <p>
 * The important pieces in this tool for the digital signature are the {@link #signPdf()} method and
 * the {@link pdftron.PDF.Tools.MySignatureHandler} class. The MySignatureHandler class extends
 * {@link pdftron.SDF.SignatureHandler}, and have the code that defines the digest and cipher
 * algorithms to sign the document. The signPdf() method then creates an instance of the
 * MySignatureHandler and adds it to the document and to the signature field. When the document is
 * saved, this class will be used to calculate the digital signature for the field.
 * <p>
 * Another option to sign the document is to use StdSignatureHandler (a built-in SignatureHandler in
 * PDFNet) to sign a PDF file, which can be accessed by {@link pdftron.PDF.PDFDoc#addStdSignatureHandler(String, String)}.
 */
class DigitalSignature extends Tool {

    public DigitalSignature(PDFViewCtrl ctrl) {
        super(ctrl);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mMenuTitles = new LinkedList<MenuEntry>();
    }

    @Override
    public int getMode() {
        return ToolManager.e_digital_signature;
    }

    /**
     * Create quick menu options based on a digitally signed signature field.
     */
    private static final int QM_SIG_FIELD_SIGNED = 0;
    /**
     * Create quick menu options to change the thickness size.
     */
    private static final int QM_SIG_FIELD_THICKNESS = 1;
    /**
     * Create quick menu options based on a signature field that has an image as its appearance.
     */
    private static final int QM_SIG_FIELD_IMAGE = 2;
    /**
     * Create quick menu options based on a signature field that has paths as its appearance.
     */
    private static final int QM_SIG_FIELD_PATHS = 3;

    /**
     * The signature field has no appearance set.
     */
    private static final int SIG_APPEARANCE_EMPTY = 0;
    /**
     * The signature field has paths as its appearance.
     */
    private static final int SIG_APPEARANCE_PATHS = 1;
    /**
     * The signature field has an image as its appearance.
     */
    private static final int SIG_APPEARANCE_IMAGE = 2;

    /**
     * Set the options for the quick menu based on the desired type.
     *
     * @param type the type of options to create. The values can be:
     *             <ul>
     *             <li>{@link #QM_SIG_FIELD_PATHS}</li>
     *             <li>{@link #QM_SIG_FIELD_IMAGE}</li>
     *             <li>{@link #QM_SIG_FIELD_SIGNED}</li>
     *             <li>{@link #QM_SIG_FIELD_THICKNESS}</li>
     *             </ul>
     */
    private void setQuickMenuOptions(int type) {
        mMenuTitles.clear();
        switch (type) {
            case QM_SIG_FIELD_SIGNED:
                mMenuTitles.add(new MenuEntry("field signed", getStringFromResId(R.string.tools_qm_field_signed)));
                break;
            case QM_SIG_FIELD_THICKNESS:
                mMenuTitles.add(new MenuEntry("1pt"));
                mMenuTitles.add(new MenuEntry("3pt"));
                mMenuTitles.add(new MenuEntry("5pt"));
                mMenuTitles.add(new MenuEntry("7pt"));
                break;
            case QM_SIG_FIELD_IMAGE:
                mMenuTitles.add(new MenuEntry("delete", getStringFromResId(R.string.tools_qm_delete)));
                mMenuTitles.add(new MenuEntry("sign and save", getStringFromResId(R.string.tools_qm_sign_and_save)));
                break;
            case QM_SIG_FIELD_PATHS:
                mMenuTitles.add(new MenuEntry("thickness", getStringFromResId(R.string.tools_qm_thickness)));
                mMenuTitles.add(new MenuEntry("color", getStringFromResId(R.string.tools_qm_color)));
                mMenuTitles.add(new MenuEntry("delete", getStringFromResId(R.string.tools_qm_delete)));
                mMenuTitles.add(new MenuEntry("sign and save", getStringFromResId(R.string.tools_qm_sign_and_save)));
                break;
        }
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        super.onSingleTapConfirmed(e);

        int x = (int) (e.getX() + 0.5);
        int y = (int) (e.getY() + 0.5);

        // Let's stay on pan tool before being certain we have the correct annotation
        mNextToolMode = ToolManager.e_pan;

        if (mAnnot != null) {
            // Check if the annotation in question is still the same
            Annot tempAnnot = mPDFView.getAnnotationAt(x, y);

            if (mAnnot.equals(tempAnnot)) {
                mNextToolMode = ToolManager.e_digital_signature;

                try {
                    Widget widget = new Widget(mAnnot);
                    if (widget.getField().getValue() != null) {
                        // Field is already signed
                        showSignatureInfo();
                    } else {
                        int type = getAnnotSignatureType();
                        switch (type) {
                            case SIG_APPEARANCE_IMAGE:
                                setQuickMenuOptions(QM_SIG_FIELD_IMAGE);
                                break;
                            case SIG_APPEARANCE_PATHS:
                                setQuickMenuOptions(QM_SIG_FIELD_PATHS);
                                break;
                            case SIG_APPEARANCE_EMPTY:
                            default:
                                showDigitalSignatureDialog();
                                break;
                        }
                        showMenu(mMenuTitles, getAnnotRect());
                    }

                } catch (Exception ex) {
                    mAnnot = null;
                    mNextToolMode = ToolManager.e_pan;
                }

            } else {
                mAnnot = null;
            }
        }

        return false;
    }

    @Override
    protected void onQuickMenuClicked(int menu_id, String menu_type) {
        super.onQuickMenuClicked(menu_id, menu_type);

        if (mAnnot != null) {
            String str = menu_type.toLowerCase();

            if (str.equals("color")) {
                // ---------------------------------------------------------------------------------
                // Change color --------------------------------------------------------------------

                // Get current color to initialize the dialog color picker
                int color_int = getAnnotPathColor();
                final DialogColorPicker d = new DialogColorPicker(mPDFView.getContext(), color_int);
                d.setAlphaSliderVisible(false);

                d.setButton(DialogInterface.BUTTON_POSITIVE, getStringFromResId(R.string.tools_misc_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        int c = d.getColor();
                        double r = (double) Color.red(c) / 255;
                        double g = (double) Color.green(c) / 255;
                        double b = (double) Color.blue(c) / 255;

                        try {
                            // Locks the document first as accessing annotation/doc
                            // information isn't thread safe.
                            mPDFView.docLock(true);

                            Obj app = mAnnot.getAppearance();
                            if (app != null) {
                                ElementReader reader = new ElementReader();
                                Element formElement = getFirstElementUsingReader(reader, app, Element.e_form);
                                if (formElement != null) {
                                    Obj o = formElement.getXObject();
                                    ElementReader objReader = new ElementReader();
                                    Element pathElement = getFirstElementUsingReader(objReader, o, Element.e_path);
                                    if (pathElement != null) {
                                        ElementWriter writer = new ElementWriter();
                                        writer.begin(o);
                                        GState gs = pathElement.getGState();
                                        gs.setStrokeColorSpace(ColorSpace.createDeviceRGB());
                                        gs.setStrokeColor(new ColorPt(r, g, b));
                                        writer.writeElement(pathElement);
                                        writer.end();
                                    }
                                    objReader.end();
                                }
                                reader.end();
                            }

                            mAnnot.refreshAppearance();
                            mPDFView.update(mAnnot, mAnnotPageNum);

                        } catch (Exception e) {
                            // Error while setting color of hand signature

                        } finally {
                            mPDFView.docUnlock();
                        }

                        showMenu(mMenuTitles, getAnnotRect());
                    }
                });
                d.setButton(DialogInterface.BUTTON_NEGATIVE, getStringFromResId(R.string.tools_misc_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showMenu(mMenuTitles, getAnnotRect());
                    }
                });

                d.show();

            } else if (str.equals("thickness")) {
                // ---------------------------------------------------------------------------------
                // Open quick menu for thickness ---------------------------------------------------

                setQuickMenuOptions(QM_SIG_FIELD_THICKNESS);
                showMenu(mMenuTitles, getAnnotRect());

            } else if (str.endsWith("pt")) {
                // ---------------------------------------------------------------------------------
                // Change thickness ----------------------------------------------------------------

                try {
                    mPDFView.docLock(true);

                    String pt_str = str.replace("pt", "");
                    float thickness = Float.valueOf(pt_str);

                    Obj app = mAnnot.getAppearance();
                    if (app != null) {
                        ElementReader reader = new ElementReader();
                        Element formElement = getFirstElementUsingReader(reader, app, Element.e_form);
                        if (formElement != null) {
                            Obj o = formElement.getXObject();
                            ElementReader objReader = new ElementReader();
                            Element pathElement = getFirstElementUsingReader(objReader, o, Element.e_path);
                            if (pathElement != null) {
                                ElementWriter writer = new ElementWriter();
                                writer.begin(o);
                                GState gs = pathElement.getGState();
                                gs.setLineWidth(thickness);
                                writer.writeElement(pathElement);
                                writer.end();
                            }
                            objReader.end();
                        }
                        reader.end();
                    }

                    mAnnot.refreshAppearance();
                    mPDFView.update(mAnnot, mAnnotPageNum);

                } catch (Exception e) {
                    // Error while setting thickness of hand signature

                } finally {
                    mPDFView.docUnlock();
                }

                setQuickMenuOptions(QM_SIG_FIELD_PATHS);
                showMenu(mMenuTitles, getAnnotRect());

            } else if (str.equals("delete")) {
                // ---------------------------------------------------------------------------------
                // Delete appearance ---------------------------------------------------------------

                mNextToolMode = ToolManager.e_pan;

                try {
                    // Locks the document first as accessing annotation/doc
                    // information isn't thread safe.
                    mPDFView.docLock(true);

                    mAnnot.getSDFObj().erase("AP");
                    mAnnot.refreshAppearance();
                    mPDFView.update(mAnnot, mAnnotPageNum);

                } catch (Exception e) {
                    // Error while deleting signature appearance

                } finally {
                    mPDFView.docUnlock();
                }

            } else if (str.equals("sign and save")) {
                // ---------------------------------------------------------------------------------
                // Sign the document ---------------------------------------------------------------

                // Digitally sign the document with the signature field using the appearance that
                // was set (paths or image).
                signPdf();
            }

            mPDFView.waitForRendering();

        } else {
            mNextToolMode = ToolManager.e_pan;
        }
    }

    private void showDigitalSignatureDialog() {
        int annotWidth = 0, annotHeight = 0;
        try {
            annotWidth = (int) mAnnot.getRect().getWidth();
            annotHeight = (int) mAnnot.getRect().getHeight();
        } catch (Exception e) {
            // Do nothing...
        }

        // Provide dialog/view to capture signature
        final DialogSignatureAppearancePicker handSignaturePicker = new DialogSignatureAppearancePicker(mPDFView.getContext());
        handSignaturePicker.setupSignatureViewSize(annotWidth, annotHeight);
        handSignaturePicker.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (!handSignaturePicker.suppressDismissListener) {
                    // Go back to pan tool
                    mPDFView.setTool(mPDFView.getToolManager().createTool(ToolManager.e_pan, mPDFView, DigitalSignature.this));
                }
            }
        });

        handSignaturePicker.setButton(DialogInterface.BUTTON_NEGATIVE, getStringFromResId(R.string.tools_misc_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Signature flow was cancelled, so go back to pan mode. The dismiss listener takes
                // care of changing modes.
            }
        });
        handSignaturePicker.setButton(DialogInterface.BUTTON_POSITIVE, getStringFromResId(R.string.tools_qm_sign_and_save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                handSignaturePicker.suppressDismissListener = true;

                boolean error = false;

                // Apply appearance to the signature field
                try {
                    mPDFView.docLock(true);

                    PDFDoc doc = mPDFView.getDoc();
                    if (handSignaturePicker.useImageAsSignature) {
                        String imageFilePath = handSignaturePicker.getSignatureImageFilePath();
                        if (!imageFilePath.equals("")) {
                            applyAppearance(doc, mAnnot, imageFilePath);
                        } else {
                            mNextToolMode = ToolManager.e_pan;
                            Toast.makeText(mPDFView.getContext(), getStringFromResId(R.string.tools_digitalsignature_error_invalid_image_path), Toast.LENGTH_LONG).show();
                            error = true;
                        }
                    } else {
                        // Get paths
                        LinkedList<LinkedList<PointF>> paths = handSignaturePicker.getSignaturePaths();
                        applyAppearance(doc, mAnnot, paths);
                    }

                    mPDFView.update(mAnnot, mAnnotPageNum);

                } catch (Exception e) {
                    // Error while setting hand signature
                    mNextToolMode = ToolManager.e_pan;
                    Toast.makeText(mPDFView.getContext(), getStringFromResId(R.string.tools_digitalsignature_error_appearance), Toast.LENGTH_LONG).show();
                    error = true;

                } finally {
                    mPDFView.docUnlock();
                }

                if (!error) {
                    // Digitally sign the document with the signature field using the appearance that
                    // was set (paths or image).
                    signPdf();
                }
            }
        });
        handSignaturePicker.setButton(DialogInterface.BUTTON_NEUTRAL, getStringFromResId(R.string.tools_digitalsignature_use_appearance), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                ((DialogSignatureAppearancePicker) dialogInterface).suppressDismissListener = true;

                // Apply signature paths to the signature field
                try {
                    mPDFView.docLock(true);

                    PDFDoc doc = mPDFView.getDoc();

                    if (handSignaturePicker.useImageAsSignature) {
                        String imageFilePath = handSignaturePicker.getSignatureImageFilePath();
                        if (!imageFilePath.equals("")) {
                            applyAppearance(doc, mAnnot, imageFilePath);
                            setQuickMenuOptions(QM_SIG_FIELD_IMAGE);
                        } else {
                            mNextToolMode = ToolManager.e_pan;
                        }
                    } else {
                        // Get paths
                        LinkedList<LinkedList<PointF>> paths = handSignaturePicker.getSignaturePaths();
                        applyAppearance(doc, mAnnot, paths);
                        setQuickMenuOptions(QM_SIG_FIELD_PATHS);
                    }

                    mPDFView.update(mAnnot, mAnnotPageNum);

                } catch (Exception e) {
                    // Error while setting hand signature
                    mNextToolMode = ToolManager.e_pan;

                } finally {
                    mPDFView.docUnlock();
                }

                showMenu(mMenuTitles, getAnnotRect());
            }
        });
        handSignaturePicker.show();

        // Let's disable the Sign and Use Appearance buttons since we do not have any paths or
        // images yet. The dialog itself will enable the buttons in case the user draws something.
        handSignaturePicker.enableDialogButtons(false);
    }

    private final static String DEFAULT_FILE_NAME_SIGNED = Environment.getExternalStorageDirectory().getAbsolutePath() + "/sample_signed_0.pdf";

    protected void signPdf() {
        String fileName;
        // Try to use the current doc filename
        try {
            fileName = mPDFView.getDoc().getFileName();
            if (fileName == null || fileName.length() == 0) {
                fileName = DEFAULT_FILE_NAME_SIGNED;
            } else {
                String s = fileName.substring(0, fileName.lastIndexOf("."));
                fileName = s + "_signed_0.pdf";
            }
        } catch (Exception e) {
            fileName = DEFAULT_FILE_NAME_SIGNED;
        }
        
        // Check for existing signed files and pick up a new name
        // so to not overwrite them.
        int i = 1;
        do {
            File signedFile = new File(fileName);
            if (signedFile.exists()) {
                String s = fileName.substring(0, fileName.lastIndexOf("_"));
                fileName = s + "_" + (i++) + ".pdf";
            } else {
                break;
            }
        } while (true);

        int currPage = mPDFView.getCurrentPage();
        boolean reopenDoc = false;
        boolean docLocked = false;
        try {
            // Obtain the signature widget via Annotation
            Widget widget = new Widget(mAnnot);
            PDFDoc doc = mPDFView.getDoc();

            // Try to lock the document for saving
            docLocked = mPDFView.docTryLock(0);
            if (docLocked) {
                // There are two options to digitally sign the document with PDFNet:
                // 1. The full version has a built-in signature handler, which can be used by
                // calling PDFDoc.addStdSignatureHandler(). This way you don't need to extend the
                // SignatureHandler interface and don't need to include any cryptographic libraries
                // to your project (eg, Spongy Castle).
                // 2. In case you are using the standard version, then you will have to create a
                // class that extends the SignatureHandler interface and code your own code that
                // defines the digest and cipher algorithms to sign the document.

                // If you are using the full version, you can use the code below:
//                InputStream is = mPDFView.getContext().getResources().getAssets().open("pdftron_certificate.pfx");
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                int reads = is.read();
//                while (reads != -1) {
//                    baos.write(reads);
//                    reads = is.read();
//                }
//                long sigHandlerId = doc.addStdSignatureHandler(baos.toByteArray(), "password");

                // In case your are using the standard version, then you can use the following
                // implementation:

                // Create a new instance of the SignatureHandler.
                MySignatureHandler sigCreator = new MySignatureHandler(mPDFView.getContext().getResources(), "password");
                // Add the SignatureHandler instance to PDFDoc, making sure to keep track of
                // it using the ID returned.
                long sigHandlerId = doc.addSignatureHandler(sigCreator);


                // Tell PDFNet to use the SignatureHandler created to sign the new signature
                // form field.
                Field sigField = widget.getField();
                Obj sigDict = sigField.useSignatureHandler(sigHandlerId);

                // Add more information to the signature dictionary
                sigDict.putName("SubFilter", "adbe.pkcs7.detached");
                sigDict.putString("Name", "PDFTron");
                sigDict.putString("Location", "Vancouver, BC");
                sigDict.putString("Reason", "Document verification.");

                doc.save(fileName, SDFDoc.e_incremental, null);
                // Need to unlock before closing the document, since closeDoc() will try
                // to lock the document.
                mPDFView.docUnlock();
                docLocked = false;

                mPDFView.closeDoc();

                mPDFView.update();
                mPDFView.invalidate();

                reopenDoc = true;
                Toast.makeText(mPDFView.getContext(), String.format(getStringFromResId(R.string.tools_digitalsignature_msg_saved), fileName), Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(mPDFView.getContext(), getStringFromResId(R.string.tools_digitalsignature_msg_file_locked), Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Toast.makeText(mPDFView.getContext(), String.format(getStringFromResId(R.string.tools_digitalsignature_msg_failed_to_save), e.getMessage()), Toast.LENGTH_LONG).show();

        } finally {
            if (docLocked) {
                mPDFView.docUnlock();
            }
        }

        if (reopenDoc) {
            try {
                PDFDoc doc = new PDFDoc(fileName);
                mPDFView.setDoc(doc);
                mPDFView.setCurrentPage(currPage);
            } catch (PDFNetException e) {
                // Do nothing...
            }
        }
    }

    protected void showSignatureInfo() {
        // Get some information from the /V entry
        if (mAnnot != null) {
            try {
                Widget widget = new Widget(mAnnot);
                Obj sigDict = widget.getField().getValue();
                if (sigDict != null) {
                    String location = sigDict.findObj("Location").getAsPDFText();
                    String reason = sigDict.findObj("Reason").getAsPDFText();
                    String name = sigDict.findObj("Name").getAsPDFText();

                    DialogSignatureInfo dialog = new DialogSignatureInfo(mPDFView.getContext());
                    dialog.setLocation(location);
                    dialog.setReason(reason);
                    dialog.setName(name);
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            mAnnot = null;
                            mNextToolMode = ToolManager.e_pan;
                        }
                    });
                    dialog.show();
                }
            } catch (Exception e) {
                // Do nothing...
            }
        }
    }

    protected Element getFirstElementUsingReader(ElementReader reader, Obj obj, int type) {
        try {
            mPDFView.docLockRead();
            if (obj != null) {
                reader.begin(obj);
                Element element;
                while ((element = reader.next()) != null) {
                    if (element.getType() == type) {
                        return element;
                    }
                }
            }
        } catch (Exception e) {
            Log.v("PDFNet", e.getMessage());
        } finally {
            mPDFView.docUnlockRead();
        }

        return null;
    }

    protected void applyAppearance(PDFDoc doc, Annot annot, LinkedList<LinkedList<PointF>> pathList) throws Exception {
        try {
            // Obtain the signature widget via Annotation
            Widget widget = new Widget(annot);

            // Add the signature appearance
            ElementWriter writer = new ElementWriter();
            ElementBuilder builder = new ElementBuilder();

            writer.begin(doc);

            builder.pathBegin();

            ListIterator<LinkedList<PointF>> listIteratorPaths = pathList.listIterator(0);
            while (listIteratorPaths.hasNext()) {
                LinkedList<PointF> path = listIteratorPaths.next();
                ListIterator<PointF> listIteratorPoints = path.listIterator(0);
                boolean firstPoint = true;
                while (listIteratorPoints.hasNext()) {
                    PointF p = listIteratorPoints.next();
                    if (firstPoint) {
                        builder.moveTo(p.x, p.y);
                        firstPoint = false;
                    } else {
                        builder.lineTo(p.x, p.y);
                    }
                }
            }

            Element element = builder.pathEnd();
            element.setPathStroke(true);
            // Set default line color
            element.getGState().setStrokeColorSpace(ColorSpace.createDeviceRGB());
            element.getGState().setStrokeColor(new ColorPt(1, 0, 0));
            // Set default line thickness
            element.getGState().setLineWidth(1);
            element.getGState().setLineCap(GState.e_round_cap);
            element.getGState().setLineJoin(GState.e_round_join);
            writer.writeElement(element);

            Obj obj = writer.end();

            obj.putRect("BBox", 0, 0, widget.getRect().getWidth(), widget.getRect().getHeight());
            obj.putName("Subtype", "Form");
            obj.putName("Type", "XObject");
            writer.begin(doc);
            element = builder.createForm(obj);
            writer.writePlacedElement(element);
            obj = writer.end();
            obj.putRect("BBox", 0, 0, widget.getRect().getWidth(), widget.getRect().getHeight());
            obj.putName("Subtype", "Form");
            obj.putName("Type", "XObject");

            widget.setAppearance(obj);
            widget.refreshAppearance();

        } catch (Exception e) {
            throw e;
        }
    }

    protected void applyAppearance(PDFDoc doc, Annot annot, String imageFilePath) throws Exception {
        try {
            // Obtain the signature widget via Annotation
            Widget widget = new Widget(annot);

            // Add the signature appearance
            ElementWriter writer = new ElementWriter();
            ElementBuilder builder = new ElementBuilder();
            writer.begin(doc, true);

            Image sigImg = Image.create(doc, imageFilePath);
            double w = sigImg.getImageWidth();
            double h = sigImg.getImageHeight();

            // Scaling the image to fit in the widget, centered and with preserved aspect ratio, is
            // quite complicated. Creating an image with a matrix with scale factors of pixel width
            // and pixel high in the horizontal and vertical directions, respectively, will create
            // an image that fills the widget.
            Rect widgetRect = widget.getRect();

            // We need the width to height ratio of both the widget and the image
            double formRatio = widgetRect.getWidth() / widgetRect.getHeight();
            double imageRatio = w / h;

            double widthAdjust = 1.0f;
            double heightAdjust = 1.0f;

            // If the form has a higher width to height ratio than the image, that means the image
            // can scale further width-wise. We therefore have to limit the scaling in that
            // direction by the ratio of the ratios...
            if (imageRatio < formRatio) {
                widthAdjust = imageRatio / formRatio;
            } else if (imageRatio > formRatio) {
                // If the form has a higher height to width ratio than the image, that means the
                // image can scale further height-wise. So in this case we limit the scaling of the
                // height in the same way.
                heightAdjust = formRatio / imageRatio;
            }

            // Now, we want to calculate the horizontal or vertical translation (we should only
            // need one of them). The image will be scaled by the smallest of the the rations
            // between the widgets and images width or height
            double horzTranslate = 0.0f;
            double vertTranslate = 0.0f;
            double widthRatio = widgetRect.getWidth() / w;      // The scale needed to fit width
            double heightRatio = widgetRect.getHeight() / h;    // The scale needed to fit height

            double scale2 = Math.min(widthRatio, heightRatio);  // We pick the smallest of them as our scale factor

            // We calculate the scaling in page space, which is half of the width or height
            // difference between the widget and scaled image.
            horzTranslate = (widgetRect.getWidth() - (w * scale2)) / 2;
            vertTranslate = (widgetRect.getHeight() - (h * scale2)) / 2;

            // The widget will scale width and height internally, so we need to add a transformation
            // matrix to the image to make it show up in the right position.
            // If you use the identity matrix, the image won't show up at all. We also need to
            // adjust the scaling of the image with the ratio from before.
            // Finally, the translation needs to happen in the space of the widget, as opposed to
            // page space. Therefore, we need to remove the scaling factor, but keep the width or
            // height adjustment.

            // Conceptually, assume you have a square image, and a widget that is 3 times wider than
            // it is height. The image is then scaled by its width and height, and then again by the
            // widget that will scale it to it's width and height. So, the image will now be 3 times
            // as wide as it is high. Therefore, what width adjust does it will change the initial
            // scaling of the width to be 1 3rd of the image's width, so that with the scaling from
            // the widget, the total width scaling is the same as the height scaling. Similarly, the
            // translation needs to operate in the widget's scaled space.
            Matrix2D mtx = new Matrix2D(w * widthAdjust, 0, 0, h * heightAdjust, horzTranslate * widthAdjust / scale2, vertTranslate * heightAdjust / scale2);


//            // Check for page rotation to apply correct transformation matrix
//            int pageRotation = mPDFView.getDoc().getPage(mAnnotPageNum).getRotation();
//            double deg2rad = 3.1415926535 / 180.0;
//            Matrix2D mtx = new Matrix2D(w, 0, 0, h, 0, 0);
//            if (pageRotation == Page.e_90) {
//                mtx = mtx.translate(w, 0);
//                Matrix2D mtx_rot = Matrix2D.rotationMatrix(-90 * deg2rad);
//                mtx = mtx.multiply(mtx_rot);
//            } else if (pageRotation == Page.e_270) {
//                mtx = mtx.translate(0, h);
//                Matrix2D mtx_rot = Matrix2D.rotationMatrix(90 * deg2rad);
//                mtx = mtx.multiply(mtx_rot);
//            }

            Element element = builder.createImage(sigImg, mtx);
            element.getGState().setTransform(mtx);
            writer.writePlacedElement(element);
            Obj obj = writer.end();
            obj.putRect("BBox", 0, 0, w, h);
            obj.putName("Subtype", "Form");
            obj.putName("Type", "XObject");
            writer.begin(doc);
            element = builder.createForm(obj);
            writer.writePlacedElement(element);
            obj = writer.end();
            obj.putRect("BBox", 0, 0, w, h);
            obj.putName("Subtype", "Form");
            obj.putName("Type", "XObject");

            widget.setAppearance(obj);
            widget.refreshAppearance();
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Get the color of the signature path.
     * <p>
     * Note: this method read-locks the document internally.
     *
     * @return the signature path color
     */
    private int getAnnotPathColor() {
        int pathColor = 0;

        try {
            // Locks the document first as accessing annotation/doc information isn't thread safe.
            mPDFView.docLockRead();

            ColorPt color = new ColorPt();

            Obj app = mAnnot.getAppearance();
            if (app != null) {
                ElementReader reader = new ElementReader();
                Element formElement = getFirstElementUsingReader(reader, app, Element.e_form);
                if (formElement != null) {
                    Obj o = formElement.getXObject();
                    ElementReader objReader = new ElementReader();
                    Element pathElement = getFirstElementUsingReader(objReader, o, Element.e_path);
                    if (pathElement != null) {
                        GState gs = pathElement.getGState();
                        color = gs.getStrokeColor();
                    }
                    objReader.end();
                }
                reader.end();
            }

            int r = (int) Math.floor(color.get(0) * 255 + 0.5);
            int g = (int) Math.floor(color.get(1) * 255 + 0.5);
            int b = (int) Math.floor(color.get(2) * 255 + 0.5);
            pathColor = Color.argb(255, r, g, b);

        } catch (Exception e) {
            pathColor = Color.argb(255, 0, 0, 0);

        } finally {
            mPDFView.docUnlockRead();
        }

        return pathColor;
    }

    /**
     * Get the signature appearance type.
     * <p>
     * Note: this method read-locks the document internally.
     *
     * @return the signature appearance type. It can be one of the following:
     * <ul>
     * <li>{@link #SIG_APPEARANCE_EMPTY}</li>
     * <li>{@link #SIG_APPEARANCE_IMAGE}</li>
     * <li>{@link #SIG_APPEARANCE_PATHS}</li>
     * </ul>
     */
    private int getAnnotSignatureType() {
        int elementType = SIG_APPEARANCE_EMPTY;
        try {
            // Locks the document first as accessing annotation/doc information isn't thread safe.
            mPDFView.docLockRead();

            Obj app = mAnnot.getAppearance();
            if (app != null) {
                ElementReader reader = new ElementReader();
                Element formElement = getFirstElementUsingReader(reader, app, Element.e_form);
                if (formElement != null) {
                    Obj o = formElement.getXObject();
                    ElementReader objReader = new ElementReader();
                    Element element = getFirstElementUsingReader(objReader, o, Element.e_path);
                    if (element != null) {
                        elementType = SIG_APPEARANCE_PATHS;
                    } else {
                        element = getFirstElementUsingReader(objReader, o, Element.e_image);
                        if (element != null) {
                            elementType = SIG_APPEARANCE_IMAGE;
                        }
                    }
                    objReader.end();
                }
                reader.end();
            }

        } catch (Exception e) {
            elementType = SIG_APPEARANCE_EMPTY;

        } finally {
            mPDFView.docUnlockRead();
        }

        return elementType;
    }
}

class MySignatureHandler extends SignatureHandler {

    private ArrayList<Byte> m_data;
    private String m_pfx;
    private String m_password;
    private Resources m_resources;

    public MySignatureHandler(String pfx, String password) {
        this.m_pfx = pfx;
        this.m_resources = null;
        init(password);
    }

    public MySignatureHandler(Resources res, String password) {
        this.m_pfx = "";
        this.m_resources = res;
        init(password);
    }

    private void init(String password) {
        this.m_password = password;
        m_data = new ArrayList<Byte>();
    }

    @Override
    public String getName() throws PDFNetException {
        return ("Adobe.PPKLite");
    }

    @Override
    public void appendData(byte[] data) throws PDFNetException {
        for (byte b : data) {
            m_data.add(b);
        }
    }

    @Override
    public boolean reset() throws PDFNetException {
        m_data.clear();
        return true;
    }

    @Override
    public byte[] createSignature() throws PDFNetException {
        try {
            java.security.Security.addProvider(new BouncyCastleProvider());
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            InputStream is;
            if (m_resources != null) {
                is = m_resources.getAssets().open("pdftron_certificate.pfx");
            } else {
                is = new FileInputStream(m_pfx);
            }
            keyStore.load(is, m_password.toCharArray());
            String alias = keyStore.aliases().nextElement();
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, m_password.toCharArray());
            Certificate[] certChain = keyStore.getCertificateChain(alias);
            is.close();

            Store certStore = new JcaCertStore(Arrays.asList(certChain));
            CMSSignedDataGenerator sigGen = new CMSSignedDataGenerator();
            ContentSigner signer = new JcaContentSignerBuilder("SHA1withRSA").setProvider("SC").build(privateKey);
            sigGen.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(
                    new JcaDigestCalculatorProviderBuilder().setProvider("SC").build()).build(signer, (X509Certificate) certChain[0]));
            sigGen.addCertificates(certStore);
            byte[] byteData = new byte[m_data.size()];
            for (int i = 0; i < m_data.size(); i++) {
                byteData[i] = m_data.get(i);
            }
            CMSTypedData data = new CMSProcessableByteArray(byteData);
            CMSSignedData sigData = sigGen.generate(data, false);

            return (sigData.getEncoded());

        } catch (Exception ex) {
            // Error while creating signature
        }

        return null;
    }
}

class DialogSignatureAppearancePicker extends AlertDialog {

    private Context mContext;

    private RelativeLayout mSignatureContainer;
    private HandSignatureView mHandSignatureView;
    private ImageView mImageSignatureView;
    private String mImageFilePath;

    public boolean suppressDismissListener = false;

    public boolean useImageAsSignature = false;

    protected DialogSignatureAppearancePicker(Context context) {
        super(context);
        init(context);
    }

    protected DialogSignatureAppearancePicker(Context context, int theme) {
        super(context, theme);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
    }

    public void setupSignatureViewSize(int width, int height) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.tools_dialog_signatureappearancepicker, null);
        mSignatureContainer = (RelativeLayout) view.findViewById(R.id.tools_dialog_signatureappearancepicker_drawcontainer);

        // Create the signature view and adjust its size
        mHandSignatureView = new HandSignatureView(mContext, this);
        // Set the desired size of the signature field (we want the signature view to have the same
        // aspect ratio of the signature field on the document)
        mHandSignatureView.setDesiredSize(width, height);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mHandSignatureView.setLayoutParams(layoutParams);
        mSignatureContainer.addView(mHandSignatureView);

        Button btnErase = (Button) view.findViewById(R.id.tools_dialog_signatureappearancepicker_btn_erase);
        // Event for erase signature button
        btnErase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetSignatureView();
            }
        });

        Button btnPickImage = (Button) view.findViewById(R.id.tools_dialog_signatureappearancepicker_btn_pick);
        // Event for pick image button
        btnPickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });

        setTitle(mContext.getString(R.string.tools_digitalsignature_adding_signature));
        setIcon(0);
        setView(view);
    }

    private void resetSignatureView() {
        mHandSignatureView.eraseSignature();
        mHandSignatureView.enableInput(true);
        useImageAsSignature = false;
        mImageFilePath = "";
        if (mImageSignatureView != null) {
            mSignatureContainer.removeView(mImageSignatureView);
        }
        enableDialogButtons(false);
    }

    private void pickImage() {
        final DialogImageFilePicker filePicker = new DialogImageFilePicker(mContext/*, android.R.style.Theme_NoTitleBar*/);
        //filePicker.setListener(this);
        filePicker.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mImageFilePath = filePicker.getImageFilePath();
                if (!mImageFilePath.equals("")) {
                    if (mImageSignatureView != null) {
                        mSignatureContainer.removeView(mImageSignatureView);
                    }
                    mHandSignatureView.eraseSignature();
                    mHandSignatureView.enableInput(false);
                    useImageAsSignature = true;
                    enableDialogButtons(true);

                    Bitmap image = BitmapFactory.decodeFile(mImageFilePath);
                    mImageSignatureView = new ImageView(mContext);
                    // Stretch the image so it looks similar to the final result, when the image
                    // is added to the field.
                    mImageSignatureView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    mImageSignatureView.setImageBitmap(image);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mHandSignatureView.getWidth(), mHandSignatureView.getHeight());
                    //RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.CENTER_IN_PARENT);
                    mImageSignatureView.setLayoutParams(params);
                    mSignatureContainer.addView(mImageSignatureView);
                }
            }
        });
        filePicker.setButton(DialogInterface.BUTTON_NEGATIVE, mContext.getString(R.string.tools_misc_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // It is necessary to have this button set so it appears in the dialog.
                // The cancel action is covered on the dismiss listener.
            }
        });
        filePicker.show();
    }

    public LinkedList<LinkedList<PointF>> getSignaturePaths() {
        if (mHandSignatureView != null) {
            return mHandSignatureView.getSignaturePaths();
        } else {
            return new LinkedList<LinkedList<PointF>>();
        }
    }

    public String getSignatureImageFilePath() {
        return mImageFilePath;
    }

    public void enableDialogButtons(boolean enable) {
        getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(enable);
        getButton(DialogInterface.BUTTON_NEUTRAL).setEnabled(enable);
    }
}

class HandSignatureView extends View {

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;
    private Paint mPencilPaint;
    private int mWidth;
    private int mHeight;
    private int mDesiredWidth;
    private int mDesiredHeight;

    private LinkedList<PointF> mPathPoints;
    private LinkedList<LinkedList<PointF>> mSignaturePaths;

    private AlertDialog mDialog;

    private boolean mEnableInput = true;

    public HandSignatureView(Context context, AlertDialog dialog) {
        super(context);
        init(dialog);
    }

    public void init() {
        mPath = new Path();

        mPencilPaint = new Paint();
        mPencilPaint.setAntiAlias(true);
        mPencilPaint.setColor(Color.BLUE);
        mPencilPaint.setStyle(Paint.Style.STROKE);

        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

        mPathPoints = new LinkedList<PointF>();
        mSignaturePaths = new LinkedList<LinkedList<PointF>>();

        mDesiredWidth = -1;
        mDesiredHeight = -1;

        mDialog = null;
    }

    public void init(AlertDialog dialog) {
        init();
        mDialog = dialog;
    }

    public void setDesiredSize(int width, int height) {
        mDesiredWidth = width <= 0 ? -1 : width;
        mDesiredHeight = height <= 0 ? -1 : height;
    }

    public void eraseSignature() {
        mPathPoints.clear();
        mSignaturePaths.clear();

        mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mPath.reset();
        invalidate();
    }

    public void enableInput(boolean enable) {
        mEnableInput = enable;
    }

    public LinkedList<LinkedList<PointF>> getSignaturePaths() {
        return mSignaturePaths;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Override onMeasure to calculate the size of the hand signature view, since we want to
        // keep the same aspect ratio of the signature field.
        int specSizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int specSizeHeight = MeasureSpec.getSize(heightMeasureSpec);

        if (mDesiredWidth == -1 || mDesiredHeight == -1) {
            setMeasuredDimension(specSizeWidth, specSizeHeight);
        } else if (mDesiredWidth >= mDesiredHeight) {
            int h = (int) (specSizeWidth * ((float) mDesiredHeight / mDesiredWidth));
            if (h == 0) {
                h = 1;
            }
            setMeasuredDimension(specSizeWidth, h);
        } else {
            int w = (int) (specSizeHeight * ((float) mDesiredWidth) / mDesiredHeight);
            if (w == 0) {
                w = 1;
            }
            setMeasuredDimension(w, specSizeHeight);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath, mPencilPaint);
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 1;

    private void touch_start(float x, float y) {
        if (mEnableInput) {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;

            mPathPoints = new LinkedList<PointF>();
            mPathPoints.add(getNormalizedPoint(x, y));

            if (mDialog != null) {
                ((DialogSignatureAppearancePicker) mDialog).enableDialogButtons(true);
            }
        }
    }

    private void touch_move(float x, float y) {
        // TODO Check for points out of bounds
        if (mEnableInput) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.lineTo(x, y);
                mX = x;
                mY = y;
                mPathPoints.add(getNormalizedPoint(x, y));
            }
        }
    }

    private void touch_up() {
        if (mEnableInput) {
            mPath.lineTo(mX, mY);
            // Commit the path to our off-screen
            mCanvas.drawPath(mPath, mPencilPaint);
            // Kill this so we don't double draw
            mPath.reset();

            mSignaturePaths.add(mPathPoints);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }

    private PointF getNormalizedPoint(float x, float y) {
        if (mDesiredWidth == -1 || mDesiredHeight == -1) {
            return new PointF(x, mHeight - y);
        } else {
            float newX = (x * mDesiredHeight) / mHeight;
            float newY = ((mHeight - y) * mDesiredWidth) / mWidth;
            return new PointF(newX, newY);
        }
    }
}

class DialogImageFilePicker extends AlertDialog {

    private ArrayList<File> mFiles = null;
    private String mImageFilePath;

    protected DialogImageFilePicker(Context context) {
        super(context);
        init(context);
    }

    protected DialogImageFilePicker(Context context, int theme) {
        super(context, theme);
        init(context);
    }

    private void init(Context context) {
        mImageFilePath = "";

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.tools_dialog_imagefilepicker, null);
        ListView listView = (ListView) view.findViewById(R.id.tools_dialog_imagefilepicker_listview);

        setTitle(context.getString(R.string.tools_digitalsignature_choose_image));
        setIcon(0);

        setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.tools_misc_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing...
            }
        });

        // TODO You might want to change the folder where to look for images
        mFiles = getImageFiles(Environment.getExternalStorageDirectory().getAbsolutePath());
        ImageFileAdapter mAdapter = new ImageFileAdapter(context, 0, mFiles);

        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (mFiles.size() > 0 && id >= 0 && id <= mFiles.size()) {
                    mImageFilePath = mFiles.get((int) id).toString();
                    dismiss();
                }
            }
        });

        setView(view);
    }

    public String getImageFilePath() {
        return mImageFilePath;
    }

    private ArrayList<File> getImageFiles(String location) {
        ArrayList<File> files = new ArrayList<File>();
        try {
            ImageFileUtil fu = new ImageFileUtil();
            TreeSet<File> image_files = fu.findAllImages(location);
            files.addAll(image_files);
        } catch (Exception e) {
            files = null;
        }
        return files;
    }

    class ImageFileUtil {

        class FileCompare implements Comparator<File> {
            public int compare(File f1, File f2) {
                return f1.getAbsolutePath().toLowerCase().compareTo(f2.getAbsolutePath().toLowerCase());
            }
        }

        TreeSet<File> mAllImages = new TreeSet<File>(new FileCompare());

        public ImageFileUtil() {
        }

        private void findAllImagesHelper(File dir) {
            if (dir.isDirectory()) {
                File[] files = dir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isDirectory()) {
                            findAllImagesHelper(file);
                        } else if (file.isFile() && (
                                file.getName().toLowerCase().endsWith("jpg") ||
                                        file.getName().toLowerCase().endsWith("png"))) {
                            mAllImages.add(file);
                        }
                    }
                }
            }
        }

        public TreeSet<File> findAllImages(String location) {
            File root = new File(location);
            mAllImages.clear();
            findAllImagesHelper(root);
            return mAllImages;
        }
    }

    class ImageFileAdapter extends ArrayAdapter<File> {

        private ArrayList<File> mItems;
        private Context mContext;

        public ImageFileAdapter(Context context, int textViewResourceId, ArrayList<File> items) {
            super(context, textViewResourceId, items);
            mItems = items;
            mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                v = getRowView(mContext);
            }
            File f = mItems.get(position);
            if (f != null) {
                TextView fn = (TextView) v.findViewById(R.id.tools_imagefileadapter_row_filename);
                TextView fs = (TextView) v.findViewById(R.id.tools_imagefileadapter_row_filesize);
                if (fn != null) {
                    fn.setText(f.getAbsolutePath());
                }
                if (fs != null) {
                    long bytes = f.length();
                    float mbs = (float) (bytes) / (1024 * 1024);
                    mbs = (float) (Math.round(mbs * 100)) / 100;
                    fs.setText(mbs + "MB");
                }
            }
            return v;
        }

        private View getRowView(Context context) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(R.layout.tools_imagefileadapter_row, null);
        }
    }
}

class DialogSignatureInfo extends AlertDialog {

    private TextView locationInfo;
    private TextView reasonInfo;
    private TextView nameInfo;

    public DialogSignatureInfo(Context context) {
        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.tools_dialog_signatureinfo, null);
        locationInfo = (TextView) view.findViewById(R.id.tools_dialog_signatureinfo_location);
        reasonInfo = (TextView) view.findViewById(R.id.tools_dialog_signatureinfo_reason);
        nameInfo = (TextView) view.findViewById(R.id.tools_dialog_signatureinfo_name);

        setView(view);

        setTitle(context.getString(R.string.tools_digitalsignature_signature_info));

        setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.tools_misc_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing...
            }
        });
    }

    public void setLocation(String text) {
        this.locationInfo.setText(text);
    }

    public void setReason(String reason) {
        this.reasonInfo.setText(reason);
    }

    public void setName(String name) {
        this.nameInfo.setText(name);
    }
}