//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package pdftron.PDF.Tools;

import pdftron.PDF.PDFViewCtrl;

public class ToolManager implements PDFViewCtrl.ToolManager{

    static protected final int e_pan = 1;
    static protected final int e_annot_edit = 2;
    static protected final int e_line_create = 3;
    static protected final int e_arrow_create = 4;
    static protected final int e_rect_create = 5;
    static protected final int e_oval_create = 6;
    static protected final int e_ink_create = 7;
    static protected final int e_text_annot_create = 8;
    static protected final int e_link_action = 9;
    static protected final int e_text_select = 10;
    static protected final int e_form_fill = 11;
    static protected final int e_text_create = 12;
    static protected final int e_annot_edit_line = 13;
    static protected final int e_rich_media = 14;
    static protected final int e_digital_signature = 15;

    /**
     * Creates the default tool that is the pan tool.
     */
    public PDFViewCtrl.Tool createDefaultTool(PDFViewCtrl ctrl) {
        Tool t;
        t =  new Pan(ctrl);
        t.onCreate();
        return t;
    }

    /**
     * Creates the specified tool and copies the necessary info from the
     * previous tool if provided.
     */
    public PDFViewCtrl.Tool createTool(int mode, PDFViewCtrl ctrl, PDFViewCtrl.Tool current_tool) {
        Tool t;

        try {
            switch (mode) {
            case e_pan:
                t = new Pan(ctrl);
                break;
            case e_annot_edit:
                t = new AnnotEdit(ctrl);
                break;
            case e_line_create:
                t = new LineCreate(ctrl);
                break;
            case e_rect_create:
                t = new RectCreate(ctrl);
                break;
            case e_arrow_create:
                t = new ArrowCreate(ctrl);
                break;
            case e_oval_create:
                t = new OvalCreate(ctrl);
                break;
            case e_ink_create:
                t = new FreehandCreate(ctrl);
                break;
            case e_text_annot_create:
                t = new StickyNoteCreate(ctrl);
                break;
            case e_link_action:
                t = new LinkAction(ctrl);
                break;
            case e_text_select:
                t = new TextSelect(ctrl);
                break;
            case e_form_fill:
                t = new FormFill(ctrl);
                break;
            case e_text_create:
                t = new FreeTextCreate(ctrl);
                break;
            case e_annot_edit_line:
                t = new AnnotEditLine(ctrl);
                break;
            case e_rich_media:
                t = new RichMedia(ctrl);
                break;
            case e_digital_signature:
                t = new DigitalSignature(ctrl);
                break;
            default:
                t = new Pan(ctrl);
                break;
            }
        } catch (Exception e) {
            t = (Tool)createDefaultTool(ctrl);
        } catch(OutOfMemoryError ooe) {
            t = (Tool)createDefaultTool(ctrl);
        }

        if (current_tool != null && current_tool instanceof Tool) {
            Tool ot = (Tool)current_tool;
            t.mAnnot = ot.mAnnot;
            t.mAnnotBBox = ot.mAnnotBBox;
            t.mAnnotPageNum = ot.mAnnotPageNum;
            t.mShowPageNum = ot.mShowPageNum;
            ot.onClose();   // Close the old tool; old tool can use this to clean up things.

            if (ot.getMode() != t.getMode()) {
                t.setJustCreatedFromAnotherTool();
            }

            // When creating sticky note, let annotation edit tool pop up the note dialog
            // directly, instead of showing the menu as the intermediate step.
            if (ot.getMode() == e_text_annot_create && t.getMode() == e_annot_edit) {
                AnnotEdit at = (AnnotEdit)t;
                at.setUpFromStickyCreate(true);
            }
        }

        // Class a tool's onCreate() function in which the tool can initialize things.
        t.onCreate();

        return t;
    }
}
