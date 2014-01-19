//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

#ifndef   H_CPDFDoc
#define   H_CPDFDoc

#include <C/Common/TRN_Types.h>
#include <C/PDF/TRN_Field.h>

#ifdef __cplusplus
extern "C" {
#endif

enum InsertFlag {
	e_none,				//default, do not insert bookmarks
	e_insert_bookmark	//insert bookmarks
};
    
enum ExtractFlag {
    e_forms_only,				// default, only extract form fields to FDF
    e_annots_only,				// only extract annots
    e_both                      // extract both form fields and annots
};
	
TRN_API TRN_PDFDocCreate (TRN_PDFDoc* result);
TRN_API TRN_PDFDocCreateFromSDFDoc (TRN_SDFDoc sdfdoc, TRN_PDFDoc* result);
TRN_API TRN_PDFDocCreateFromUFilePath (const TRN_UString filepath, TRN_PDFDoc* result);
TRN_API TRN_PDFDocCreateFromFilePath (const char* filepath, TRN_PDFDoc* result);
TRN_API TRN_PDFDocCreateFromFilter (TRN_Filter stream, TRN_PDFDoc* result);
TRN_API TRN_PDFDocCreateFromBuffer (const char* buf, TRN_Size buf_size, TRN_PDFDoc* result);
TRN_API TRN_PDFDocDestroy (TRN_PDFDoc doc);
TRN_API TRN_PDFDocIsEncrypted(TRN_PDFDoc doc, TRN_Bool* result);
TRN_API TRN_PDFDocInitSecurityHandler (TRN_PDFDoc doc, void* custom_data, TRN_Bool* result);
TRN_API TRN_PDFDocInitStdSecurityHandler (TRN_PDFDoc doc, const char* password, int password_sz, TRN_Bool* result);
TRN_API TRN_PDFDocGetSecurityHandler (TRN_PDFDoc doc, TRN_SecurityHandler* result);
TRN_API TRN_PDFDocSetSecurityHandler (TRN_PDFDoc doc, TRN_SecurityHandler handler);
TRN_API TRN_PDFDocRemoveSecurity(TRN_PDFDoc doc);
TRN_API TRN_PDFDocGetDocInfo(TRN_PDFDoc doc, TRN_PDFDocInfo* result );
TRN_API TRN_PDFDocGetViewPrefs(TRN_PDFDoc doc, TRN_PDFDocViewPrefs* result);
TRN_API TRN_PDFDocIsModified (const TRN_PDFDoc doc, TRN_Bool* result); 
TRN_API TRN_PDFDocIsLinearized(const TRN_PDFDoc doc, TRN_Bool* result);
TRN_API TRN_PDFDocSave(TRN_PDFDoc doc, const TRN_UString path, TRN_UInt32 flags, TRN_ProgressMonitor* progress);
TRN_API TRN_PDFDocSaveMemoryBuffer(TRN_PDFDoc doc, TRN_UInt32 flags, TRN_ProgressMonitor* progress, TRN_Size* out_buf_size, const char** result_buf); 
TRN_API TRN_PDFDocSaveStream(TRN_PDFDoc doc, TRN_Filter stream, TRN_UInt32 flags);
TRN_API TRN_PDFDocGetPageIterator(TRN_PDFDoc doc, TRN_UInt32 page_number, TRN_Iterator* result );
TRN_API TRN_PDFDocGetPage(TRN_PDFDoc doc, TRN_UInt32 page_number, TRN_Page* result);
TRN_API TRN_PDFDocPageRemove(TRN_PDFDoc doc, const TRN_Iterator page_itr);
TRN_API TRN_PDFDocPageInsert(TRN_PDFDoc doc, TRN_Iterator where, TRN_Page page);
TRN_API TRN_PDFDocInsertPages(TRN_PDFDoc dest_doc, TRN_UInt32 insert_before_page_number, TRN_PDFDoc src_doc, TRN_UInt32 start_page, TRN_UInt32 end_page, TRN_UInt32 flag, TRN_ProgressMonitor* progress);
TRN_API TRN_PDFDocInsertPageSet(TRN_PDFDoc dest_doc, TRN_UInt32 insert_before_page_number, TRN_PDFDoc src_doc, TRN_PageSet source_page_set, TRN_UInt32 flag, TRN_ProgressMonitor* progress);
TRN_API TRN_PDFDocMovePages(TRN_PDFDoc dest_doc, TRN_UInt32 move_before_page_number, TRN_PDFDoc src_doc, TRN_UInt32 start_page, TRN_UInt32 end_page, TRN_UInt32 flag, TRN_ProgressMonitor* progress);
TRN_API TRN_PDFDocMovePageSet(TRN_PDFDoc dest_doc, TRN_UInt32 move_before_page_number, TRN_PDFDoc src_doc, TRN_PageSet source_page_set, TRN_UInt32 flag, TRN_ProgressMonitor* progress);
TRN_API TRN_PDFDocPagePushFront(TRN_PDFDoc doc, TRN_Page page);
TRN_API TRN_PDFDocPagePushBack(TRN_PDFDoc doc, TRN_Page page);
TRN_API TRN_PDFDocImportPages(TRN_PDFDoc doc, const TRN_Page* page_buf, int buf_size, TRN_Bool import_bookmarks, TRN_Page* buf_result);
TRN_API TRN_PDFDocPageCreate(TRN_PDFDoc doc, const TRN_Rect* media_box, TRN_Page* result);
TRN_API TRN_PDFDocGetFirstBookmark(TRN_PDFDoc doc, TRN_Bookmark* result );
TRN_API TRN_PDFDocAddRootBookmark(TRN_PDFDoc doc, TRN_Bookmark root_bookmark);
TRN_API TRN_PDFDocGetTrailer (TRN_PDFDoc doc, TRN_Obj* result);
TRN_API TRN_PDFDocGetRoot(TRN_PDFDoc doc, TRN_Obj* result);
TRN_API TRN_PDFDocGetPages(TRN_PDFDoc doc, TRN_Obj* result);
TRN_API TRN_PDFDocGetPageCount (TRN_PDFDoc doc, int* result);
TRN_API TRN_PDFDocGetFieldIteratorBegin(TRN_PDFDoc doc, TRN_Iterator* result);
TRN_API TRN_PDFDocGetFieldIterator(TRN_PDFDoc doc, const TRN_UString field_name, TRN_Iterator* result);
TRN_API TRN_PDFDocGetField(TRN_PDFDoc doc, const TRN_UString field_name, TRN_Field* result);
TRN_API TRN_PDFDocFieldCreate(TRN_PDFDoc doc, const TRN_UString field_name, enum TRN_FieldType type, TRN_Obj field_value , TRN_Obj def_field_value, TRN_Field* result);
TRN_API TRN_PDFDocFieldCreateFromStrings(TRN_PDFDoc doc, const TRN_UString field_name, enum TRN_FieldType type, TRN_UString field_value , TRN_UString def_field_value, TRN_Field* result);
TRN_API TRN_PDFDocRefreshFieldAppearances(TRN_PDFDoc doc);
TRN_API TRN_PDFDocFlattenAnnotations(TRN_PDFDoc doc, TRN_Bool forms_only);
TRN_API TRN_PDFDocGetAcroForm(TRN_PDFDoc doc, TRN_Obj* result);
TRN_API TRN_PDFDocFDFExtract(TRN_PDFDoc doc, TRN_UInt32 flag, TRN_FDFDoc* result);
TRN_API TRN_PDFDocFDFMerge(TRN_PDFDoc doc, TRN_FDFDoc fdf_doc);
TRN_API TRN_PDFDocGetOpenAction(TRN_PDFDoc doc, TRN_Action* result);
TRN_API TRN_PDFDocSetOpenAction(TRN_PDFDoc doc, const TRN_Action action);
TRN_API TRN_PDFDocAddFileAttachment(TRN_PDFDoc doc, const TRN_UString file_key, TRN_FileSpec embeded_file);
TRN_API TRN_PDFDocGetPageLabel(TRN_PDFDoc doc, int page_num, TRN_PageLabel* result);
TRN_API TRN_PDFDocSetPageLabel(TRN_PDFDoc doc, int page_num, TRN_PageLabel* label);
TRN_API TRN_PDFDocRemovePageLabel(TRN_PDFDoc doc, int page_num);
TRN_API TRN_PDFDocGetStructTree(TRN_PDFDoc doc, TRN_STree* result);
TRN_API TRN_PDFDocHasOC(TRN_PDFDoc doc, TRN_Bool* result);
TRN_API TRN_PDFDocGetOCGs(TRN_PDFDoc doc, TRN_Obj* result);
TRN_API TRN_PDFDocGetOCGConfig(TRN_PDFDoc doc, TRN_OCGConfig* result);
TRN_API TRN_PDFDocCreateIndirectName(TRN_PDFDoc doc, const char* name, TRN_Obj* result);
TRN_API TRN_PDFDocCreateIndirectArray(TRN_PDFDoc doc, TRN_Obj* result);
TRN_API TRN_PDFDocCreateIndirectBool(TRN_PDFDoc doc, TRN_Bool value, TRN_Obj* result);
TRN_API TRN_PDFDocCreateIndirectDict(TRN_PDFDoc doc, TRN_Obj* result);
TRN_API TRN_PDFDocCreateIndirectNull(TRN_PDFDoc doc, TRN_Obj* result);
TRN_API TRN_PDFDocCreateIndirectNumber(TRN_PDFDoc doc, double value, TRN_Obj* result);
TRN_API TRN_PDFDocCreateIndirectString(TRN_PDFDoc doc, const TRN_UChar* value, TRN_UInt32 size, TRN_Obj* result);
TRN_API TRN_PDFDocCreateIndirectStringFromUString(TRN_PDFDoc doc, const TRN_UString str, TRN_Obj* result);
TRN_API TRN_PDFDocCreateIndirectStreamFromFilter(TRN_PDFDoc doc, TRN_FilterReader data, TRN_Filter filter_chain, TRN_Obj* result);
TRN_API TRN_PDFDocCreateIndirectStream(TRN_PDFDoc doc, const char* data, const TRN_Size data_size, TRN_Filter filter_chain, TRN_Obj* result);
TRN_API TRN_PDFDocGetSDFDoc (TRN_PDFDoc doc, TRN_SDFDoc* result);
TRN_API TRN_PDFDocLock(TRN_PDFDoc doc);
TRN_API TRN_PDFDocUnlock(TRN_PDFDoc doc);
TRN_API TRN_PDFDocLockRead(TRN_PDFDoc doc);
TRN_API TRN_PDFDocUnlockRead(TRN_PDFDoc doc);
TRN_API TRN_PDFDocTryLock( TRN_PDFDoc doc, TRN_Bool* result );
TRN_API TRN_PDFDocTimedLock( TRN_PDFDoc doc, int milliseconds, TRN_Bool* result );
TRN_API TRN_PDFDocTryLockRead( TRN_PDFDoc doc, TRN_Bool* result );
TRN_API TRN_PDFDocTimedLockRead( TRN_PDFDoc doc, int milliseconds, TRN_Bool* result );
TRN_API TRN_PDFDocAddHighlights(TRN_PDFDoc doc, const TRN_UString hilite);
TRN_API TRN_PDFDocIsTagged(TRN_PDFDoc doc, TRN_Bool* result);
TRN_API TRN_PDFDocHasSignatures(TRN_PDFDoc doc, TRN_Bool* result);
TRN_API TRN_PDFDocAddSignatureHandler(TRN_PDFDoc doc, TRN_SignatureHandler signature_handler, TRN_SignatureHandlerId* result);
TRN_API TRN_PDFDocAddStdSignatureHandlerFromFile(TRN_PDFDoc doc, const TRN_UString pkcs12_file, const TRN_UString pkcs12_pass, TRN_SignatureHandlerId* result);
TRN_API TRN_PDFDocAddStdSignatureHandlerFromBuffer(TRN_PDFDoc doc, const TRN_UInt8* pkcs12_buffer, const TRN_Size pkcs12_buffsize, const TRN_UString pkcs12_pass, TRN_SignatureHandlerId* result);
TRN_API TRN_PDFDocRemoveSignatureHandler(TRN_PDFDoc doc, const TRN_SignatureHandlerId signature_handler_id);
TRN_API TRN_PDFDocGetSignatureHandler(TRN_PDFDoc doc, const TRN_SignatureHandlerId signature_handler_id, TRN_SignatureHandler* result);
TRN_API TRN_PDFDocGenerateThumbnails(TRN_PDFDoc doc, TRN_UInt32 size);


// #define JDM 1
#ifdef JDM
	struct TRN_displist_;
	typedef struct TRN_displist_* TRN_DispList;

	TRN_API TRN_DispListCreate(TRN_Page page, TRN_DispList* result);
	TRN_API TRN_DispListTag(TRN_DispList lst, double* rects, TRN_Size rect_num, const char* tag, TRN_Obj prop_dict, TRN_Bool intersect_mode, TRN_Bool reshuffle, TRN_Bool* result);
	TRN_API TRN_DispListClearTags(TRN_DispList lst);
	TRN_API TRN_DispListSave(TRN_DispList lst, TRN_Page page);
	TRN_API TRN_DispListDestroy(TRN_DispList lst);
#endif


#ifdef __cplusplus
}
#endif

#endif
