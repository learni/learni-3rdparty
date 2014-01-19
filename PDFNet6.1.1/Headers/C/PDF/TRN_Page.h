//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

#ifndef   H_CPDFPage
#define   H_CPDFPage
#include <C/Common/TRN_Types.h>

#ifdef __cplusplus
extern "C" {
#endif


TRN_API TRN_PageCreate (TRN_Obj page_dict, TRN_Page* result);
TRN_API TRN_PageCopy (const TRN_Page p, TRN_Page* result);
TRN_API TRN_PageIsValid(const TRN_Page page, TRN_Bool* result);
TRN_API TRN_PageGetIndex(const TRN_Page page, int* result);

enum TRN_PageBox
{
	e_media,
	e_crop,
	e_bleed,
	e_trim,
	e_art
};

TRN_API TRN_PageGetBox(const TRN_Page page, enum TRN_PageBox type, TRN_Rect* result);
TRN_API TRN_PageSetBox(TRN_Page page, enum TRN_PageBox type, const TRN_Rect* box);
TRN_API TRN_PageGetCropBox(const TRN_Page page, TRN_Rect* result);
TRN_API TRN_PageSetCropBox(TRN_Page page, const TRN_Rect* box);
TRN_API TRN_PageGetMediaBox(const TRN_Page page, TRN_Rect* result);
TRN_API TRN_PageSetMediaBox(TRN_Page page, const TRN_Rect* box);
TRN_API TRN_PageGetVisibleContentBox(const TRN_Page page, TRN_Rect* result);

enum TRN_PageRotate 
{
	e_0,
	e_90,
	e_180,
	e_270
};

TRN_API TRN_PageGetRotation(const TRN_Page page,enum TRN_PageRotate* result);
TRN_API TRN_PageSetRotation(TRN_Page page, enum TRN_PageRotate angle);
TRN_API TRN_PageGetPageWidth(TRN_Page page, enum TRN_PageBox box_type, double* result);
TRN_API TRN_PageGetPageHeight(TRN_Page page,enum TRN_PageBox box_type, double* result);
TRN_API TRN_PageGetDefaultMatrix(const TRN_Page page, TRN_Bool flip_y, 
	enum TRN_PageBox box_type, enum TRN_PageRotate angle, TRN_Matrix2D* result);

TRN_API TRN_PageGetAnnots(const TRN_Page page, TRN_Obj* result);
TRN_API TRN_PageGetNumAnnots(const TRN_Page page, TRN_UInt32* result);
TRN_API TRN_PageGetAnnot(const TRN_Page page, TRN_UInt32 index, TRN_Annot* result);
TRN_API TRN_PageAnnotInsert(TRN_Page page,TRN_UInt32 pos, TRN_Annot annot);
TRN_API TRN_PageAnnotPushBack(TRN_Page page,TRN_Annot annot);
TRN_API TRN_PageAnnotPushFront(TRN_Page page,TRN_Annot annot);
TRN_API TRN_PageAnnotRemove(TRN_Page page,TRN_Annot annot);
TRN_API TRN_PageAnnotRemoveByIndex(TRN_Page page, TRN_UInt32 index);
TRN_API TRN_PageScale(TRN_Page page,double scale);
TRN_API TRN_PageFlattenField(TRN_Page page,TRN_Field* filed);
TRN_API TRN_PageHasTransition(const TRN_Page page, TRN_Bool* result);
TRN_API TRN_PageGetUserUnitSize(const TRN_Page page, double* result);
TRN_API TRN_PageSetUserUnitSize(TRN_Page page,double unit_size);
TRN_API TRN_PageGetResourceDict (const TRN_Page page, TRN_Obj* result);
TRN_API TRN_PageGetContents (const TRN_Page page, TRN_Obj* result);
TRN_API TRN_PageGetThumb(const TRN_Page page, TRN_Obj* result);
TRN_API TRN_PageGetSDFObj (const TRN_Page page, TRN_Obj* result);
TRN_API TRN_PageFindInheritedAttribute (const TRN_Page page, const char* attrib, TRN_Obj* result);

#ifdef __cplusplus
}
#endif

#endif
