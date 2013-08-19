//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

#ifndef H_TRN_CTYPES8
#define H_TRN_CTYPES8

#ifdef __cplusplus
extern "C" {
#endif


#ifdef SWIG 
	#define TRN_API_CALL 
	#define TRN_API_T(type) type 
	#define TRN_API TRN_Exception
#else
#ifdef _WIN32
	#define TRN_API_CALL __cdecl
	#ifdef TRN_API_EXPORT
		#define TRN_API_T(type) __declspec(dllexport) type TRN_API_CALL
		#define TRN_API TRN_API_T(TRN_Exception)
	#else
		#define TRN_API_T(type) __declspec(dllimport) type TRN_API_CALL
		#define TRN_API TRN_API_T(TRN_Exception)
	#endif
#else
	#ifdef TRN_API_EXPORT
		#define TRN_API_CALL
		#define TRN_API_T(type) __attribute__((visibility("default"))) type TRN_API_CALL
		#define TRN_API TRN_API_T(TRN_Exception)
	#else
		#define TRN_API_CALL
		#define TRN_API_T(type) type
		#define TRN_API TRN_API_T(TRN_Exception)
	#endif
#endif
#endif

#define BToTB(in) ((TRN_Bool)(in))
#define BToTBP(ptr) ((TRN_Bool*)(ptr))
#define TBToB(in) ((in)!=0)
#define TBToBP(ptr) ((bool*)(ptr))


#ifndef __cplusplus
/* #define const */
/* typedef unsigned char bool; */
#endif

#include <stdlib.h>
#include <stddef.h>

/* set alignment to 8 byte boundary  */
#pragma pack(8)  

struct TRN_exception_;
typedef struct TRN_exception_* TRN_Exception;

struct TRN_font_;
typedef struct TRN_font_* TRN_Font;

struct TRN_obj_;
typedef struct TRN_obj_* TRN_Obj;

struct TRN_sdfdoc;
typedef struct TRN_sdfdoc* TRN_SDFDoc;

struct TRN_securityhandler;
typedef struct TRN_securityhandler* TRN_SecurityHandler;

typedef TRN_SecurityHandler (*TRN_PDFNetCreateSecurityHandler) (const char* name, int key_len, int enc_code);
struct TRN_sechdlrinfo
{
	const char* m_name;
	const char* m_gui_name;
	TRN_PDFNetCreateSecurityHandler m_factory_method;
};
typedef struct TRN_sechdlrinfo TRN_SecHdlrInfo;

struct TRN_iterator;
typedef struct TRN_iterator* TRN_Iterator;

struct TRN_itrdata;
typedef struct TRN_itrdata* TRN_ItrData;

struct TRN_dictiterator;
typedef struct TRN_dictiterator* TRN_DictIterator;

struct TRN_numbertree;
typedef struct TRN_numbertree* TRN_NumberTree;

struct TRN_nametree;
typedef struct TRN_nametree* TRN_NameTree;

struct TRN_progressmonitor;
typedef struct TRN_progressmonitor* TRN_ProgressMonitor;

struct TRN_objpair
{
	TRN_Obj key;
	TRN_Obj value;
};
typedef struct TRN_objpair TRN_ObjPair;


struct TRN_fontcharcodeiterator;
typedef struct TRN_fontcharcodeiterator* TRN_FontCharCodeIterator;

struct TRN_UVector;
struct TRN_list;
typedef struct TRN_list* TRN_List;

#include <C/Common/TRN_BasicTypes.h>

struct TRN_ustring;
typedef struct TRN_ustring* TRN_UString;

struct TRN_filter;
typedef struct TRN_filter* TRN_Filter;

struct TRN_filterreader;
typedef struct TRN_filterreader* TRN_FilterReader;

struct TRN_filterwriter;
typedef struct TRN_filterwriter* TRN_FilterWriter;


struct TRN_colorpt {
	double* c; 
	double _c[4];
};

typedef struct TRN_colorpt TRN_ColorPt;


struct TRN_annot;
typedef struct TRN_annot* TRN_Annot;

struct TRN_action;
typedef struct TRN_action* TRN_Action;

struct TRN_annotborderstyle;
typedef struct TRN_annotborderstyle* TRN_AnnotBorderStyle;

struct TRN_filespec;
typedef struct TRN_filespec* TRN_FileSpec;

struct TRN_destination;
typedef struct TRN_destination* TRN_Destination;

struct TRN_bookmark;
typedef struct TRN_bookmark* TRN_Bookmark;

struct TRN_page;
typedef struct TRN_page* TRN_Page;

struct TRN_pdfdc;
typedef struct TRN_pdfdc * TRN_PDFDC;

struct TRN_pdfdcex;
typedef struct TRN_pdfdcex * TRN_PDFDCEX;

struct TRN_pdfdoc;
typedef struct TRN_pdfdoc* TRN_PDFDoc;

struct TRN_pdfdocinfo;
typedef struct TRN_pdfdocinfo* TRN_PDFDocInfo;

struct TRN_pdfdocviewprefs;
typedef struct TRN_pdfdocviewprefs* TRN_PDFDocViewPrefs;

struct TRN_pagelabel
{
	TRN_Obj mp_obj;
	int m_first_page;
	int m_last_page;
};
typedef struct TRN_pagelabel TRN_PageLabel;

struct TRN_elementbuilder;
typedef struct TRN_elementbuilder* TRN_ElementBuilder;

struct TRN_image;
typedef struct TRN_image* TRN_Image;

struct TRN_element;
typedef struct TRN_element* TRN_Element;


struct TRN_shading;
typedef struct TRN_shading* TRN_Shading;


struct TRN_field
{
	TRN_Obj leaf_node;
	TRN_ElementBuilder builder;
};
typedef struct TRN_field TRN_Field;


struct TRN_stree;
typedef struct TRN_stree* TRN_STree;

struct TRN_contentitem
{
	TRN_Obj o;
	TRN_Obj p;
};
typedef struct TRN_contentitem TRN_ContentItem;

struct TRN_selement
{
	TRN_Obj obj;
	TRN_Obj k;
};
typedef struct TRN_selement TRN_SElement;

struct TRN_classmap;
typedef struct TRN_classmap* TRN_ClassMap;

struct TRN_rolemap;
typedef struct TRN_rolemap* TRN_RoleMap;

struct TRN_attrobj;
typedef struct TRN_attrobj* TRN_AttrObj;

struct TRN_ocgconfig;
typedef struct TRN_ocgconfig* TRN_OCGConfig;

struct TRN_ocgcontext;
typedef struct TRN_ocgcontext* TRN_OCGContext;

struct TRN_ocg;
typedef struct TRN_ocg* TRN_OCG;

struct TRN_ocmd;
typedef struct TRN_ocmd* TRN_OCMD;

struct TRN_colorspace;
typedef struct TRN_colorspace* TRN_ColorSpace;

struct TRN_function;
typedef struct TRN_function* TRN_Function;

struct TRN_gstate;
typedef struct TRN_gstate* TRN_GState;

struct TRN_elementreader;
typedef struct TRN_elementreader* TRN_ElementReader;

struct TRN_elementwriter;
typedef struct TRN_elementwriter* TRN_ElementWriter;


struct TRN_fdfdoc;
typedef struct TRN_fdfdoc* TRN_FDFDoc;

struct TRN_fdffield
{
	TRN_Obj	mp_leaf_node;
	TRN_Obj mp_root_array;
};
typedef struct TRN_fdffield TRN_FDFField;


#ifdef _WIN32
struct TRN_gdiplusbitmap;
typedef struct TRN_gdiplusbitmap* TRN_GDIPlusBitmap;
#endif

struct TRN_patterncolor;
typedef struct TRN_patterncolor* TRN_PatternColor;

struct TRN_pdfdraw;
typedef struct TRN_pdfdraw* TRN_PDFDraw;

struct TRN_pdfrasterizer;
typedef struct TRN_pdfrasterizer* TRN_PDFRasterizer;

struct TRN_systemdrawingbitmap;
typedef struct TRN_systemdrawingbitmap* TRN_SystemDrawingBitmap;

struct TRN_pdfview;
typedef struct TRN_pdfview* TRN_PDFView;

struct TRN_pdfviewselection;
typedef struct TRN_pdfviewselection* TRN_PDFViewSelection;

struct TRN_pdfviewctrl;
typedef struct TRN_pdfviewctrl* TRN_PDFViewCtrl;

struct TRN_pdfviewctrlselection;
typedef struct TRN_pdfviewctrlselection* TRN_PDFViewCtrlSelection;

struct TRN_textextractor;
typedef struct TRN_textextractor* TRN_TextExtractor;

struct TRN_highlights;
typedef struct TRN_highlights* TRN_Highlights;

struct TRN_textsearch;
typedef struct TRN_textsearch* TRN_TextSearch;

struct TRN_textextractorstyle {
	void* mp_imp;
};
typedef struct TRN_textextractorstyle TRN_TextExtractorStyle;

struct TRN_textextractorword {
	const double *line, *word, *end; 
	const TRN_Unicode *uni; 
	int num, cur_num; 
	void* mp_bld;
};
typedef struct TRN_textextractorword TRN_TextExtractorWord;

struct TRN_textextractorline {
	const double *line; 
	const TRN_Unicode *uni; 
	int num, cur_num; 
	double m_direction;
	void* mp_bld;
};

typedef struct TRN_textextractorline TRN_TextExtractorLine;

struct TRN_securityhandler;

struct TRN_objset;
typedef struct TRN_objset* TRN_ObjSet;

/*
#ifdef _WIN32
#define TRN_SIGAPI __stdcall
#elif __iOS__
#define TRN_SIGAPI
#else // _WIN32
#define TRN_SIGAPI __attribute__((stdcall))
#endif // _WIN32
*/
#define TRN_SIGAPI

typedef size_t TRN_SignatureHandlerId;

typedef struct TRN_signaturedata
{
    TRN_UInt8* data;
    TRN_Size length;
}
TRN_SignatureData;
/*
typedef struct TRN_validatesignatureresult
{
    TRN_Bool valid;
    TRN_Bool wrong_handler;
    TRN_Int32 error_code;
    TRN_UString message;
}
TRN_ValidateSignatureResult;
*/
struct TRN_signaturehandler;
typedef struct TRN_signaturehandler* TRN_SignatureHandler;

struct TRN_pdfacompliance;
typedef struct TRN_pdfacompliance* TRN_PDFACompliance;

struct TRN_contentreplacer;
typedef struct TRN_contentreplacer* TRN_ContentReplacer;

struct TRN_stamper;
typedef struct TRN_stamper* TRN_Stamper;

struct TRN__pageset;
typedef struct TRN_pageset* TRN_PageSet;

struct TRN_flattener;
typedef struct TRN_flattener* TRN_Flattener;

#ifdef __iOS__
enum TRN_PaperSize {
	e_papersize_custom = 0,
	e_papersize_letter,
	e_papersize_letter_small,
	e_papersize_tabloid,
	e_papersize_ledger,
	e_papersize_legal,
	e_papersize_statement,
	e_papersize_executive,
	e_papersize_a3,
	e_papersize_a4,
	e_papersize_a4_mall,
	e_papersize_a5,
	e_papersize_b4_jis,
	e_papersize_b5_jis,
	e_papersize_folio,
	e_papersize_quarto,
	e_papersize_10x14,
	e_papersize_11x17,
	e_papersize_note,
	e_papersize_envelope_9,
	e_papersize_envelope_10,
	e_papersize_envelope_11,
	e_papersize_envelope_12,
	e_papersize_envelope_14,
	e_papersize_c_size_sheet,
	e_papersize_d_size_sheet,
	e_papersize_e_size_sheet,		
	e_papersize_envelope_dl,
	e_papersize_envelope_c5,
	e_papersize_envelope_c3,
	e_papersize_envelope_c4,
	e_papersize_envelope_c6,
	e_papersize_envelope_c65,
	e_papersize_envelope_b4,
	e_papersize_envelope_b5,
	e_papersize_envelope_b6,
	e_papersize_envelope_italy,
	e_papersize_envelope_monarch,
	e_papersize_6_3_quarters_envelope,
	e_papersize_us_std_fanfold,
	e_papersize_german_std_fanfold,
	e_papersize_german_legal_fanfold,
	e_papersize_b4_iso,
	e_papersize_japanese_postcard,
	e_papersize_9x11,
	e_papersize_10x11,
	e_papersize_15x11,
	e_papersize_envelope_invite,
	e_papersize_reserved_48,
	e_papersize_reserved_49,
	e_papersize_letter_extra,
	e_papersize_legal_extra,
	e_papersize_tabloid_extra,
	e_papersize_a4_extra,
	e_papersize_letter_transverse,
	e_papersize_a4_transverse,
	e_papersize_letter_extra_transverse,
	e_papersize_supera_supera_a4,
	e_papersize_Superb_Superb_a3,
	e_papersize_letter_plus,
	e_papersize_a4_plus,
	e_papersize_a5_transverse,
	e_papersize_b5_jis_transverse,
	e_papersize_a3_extra,
	e_papersize_a5_extra,
	e_papersize_b5_iso_extra,
	e_papersize_a2,
	e_papersize_a3_transverse,
	e_papersize_a3_extra_transverse,
	e_papersize_japanese_double_postcard,
	e_papersize_a6,
	e_papersize_japanese_envelope_kaku_2,
	e_papersize_japanese_envelope_kaku_3,
	e_papersize_japanese_envelope_chou_3,
	e_papersize_japanese_envelope_chou_4,
	e_papersize_letter_rotated,
	e_papersize_a3_rotated,
	e_papersize_a4_rotated,
	e_papersize_a5_rotated,
	e_papersize_b4_jis_rotated,
	e_papersize_b5_jis_rotated,
	e_papersize_japanese_postcard_rotated,
	e_papersize_double_japanese_postcard_rotated,
	e_papersize_a6_rotated,
	e_papersize_japanese_envelope_kaku_2_rotated,
	e_papersize_japanese_envelope_kaku_3_rotated,
	e_papersize_japanese_envelope_chou_3_rotated,
	e_papersize_japanese_envelope_chou_4_rotated,
	e_papersize_b6_jis,
	e_papersize_b6_jis_rotated,
	e_papersize_12x11,
	e_papersize_japanese_envelope_you_4,
	e_papersize_japanese_envelope_you_4_rotated,
	e_papersize_prc_16k,
	e_papersize_prc_32k,
	e_papersize_prc_32k_big,
	e_papersize_prc_envelop_1,
	e_papersize_prc_envelop_2,
	e_papersize_prc_envelop_3,
	e_papersize_prc_envelop_4,
	e_papersize_prc_envelop_5,
	e_papersize_prc_envelop_6,
	e_papersize_prc_envelop_7,
	e_papersize_prc_envelop_8,
	e_papersize_prc_envelop_9,
	e_papersize_prc_envelop_10,
	e_papersize_prc_16k_rotated,
	e_papersize_prc_32k_rotated,
	e_papersize_prc_32k_big__rotated,
	e_papersize_prc_envelop_1_rotated,
	e_papersize_prc_envelop_2_rotated,
	e_papersize_prc_envelop_3_rotated,
	e_papersize_prc_envelop_4_rotated,
	e_papersize_prc_envelop_5_rotated,
	e_papersize_prc_envelop_6_rotated,
	e_papersize_prc_envelop_7_rotated,
	e_papersize_prc_envelop_8_rotated,
	e_papersize_prc_envelop_9_rotated,
	e_papersize_prc_envelop_10_rotated,
};
#else
    enum TRN_PaperSize {
        e_custom = 0,
        e_letter,
        e_letter_small,
        e_tabloid,
        e_ledger,
        e_legal,
        e_statement,
        e_executive,
        e_a3,
        e_a4,
        e_a4_mall,
        e_a5,
        e_b4_jis,
        e_b5_jis,
        e_folio,
        e_quarto,
        e_10x14,
        e_11x17,
        e_note,
        e_envelope_9,
        e_envelope_10,
        e_envelope_11,
        e_envelope_12,
        e_envelope_14,
        e_c_size_sheet,
        e_d_size_sheet,
        e_e_size_sheet,		
        e_envelope_dl,
        e_envelope_c5,
        e_envelope_c3,
        e_envelope_c4,
        e_envelope_c6,
        e_envelope_c65,
        e_envelope_b4,
        e_envelope_b5,
        e_envelope_b6,
        e_envelope_italy,
        e_envelope_monarch,
        e_6_3_quarters_envelope,
        e_us_std_fanfold,
        e_german_std_fanfold,
        e_german_legal_fanfold,
        e_b4_iso,
        e_japanese_postcard,
        e_9x11,
        e_10x11,
        e_15x11,
        e_envelope_invite,
        e_reserved_48,
        e_reserved_49,
        e_letter_extra,
        e_legal_extra,
        e_tabloid_extra,
        e_a4_extra,
        e_letter_transverse,
        e_a4_transverse,
        e_letter_extra_transverse,
        e_supera_supera_a4,
        e_Superb_Superb_a3,
        e_letter_plus,
        e_a4_plus,
        e_a5_transverse,
        e_b5_jis_transverse,
        e_a3_extra,
        e_a5_extra,
        e_b5_iso_extra,
        e_a2,
        e_a3_transverse,
        e_a3_extra_transverse,
        e_japanese_double_postcard,
        e_a6,
        e_japanese_envelope_kaku_2,
        e_japanese_envelope_kaku_3,
        e_japanese_envelope_chou_3,
        e_japanese_envelope_chou_4,
        e_letter_rotated,
        e_a3_rotated,
        e_a4_rotated,
        e_a5_rotated,
        e_b4_jis_rotated,
        e_b5_jis_rotated,
        e_japanese_postcard_rotated,
        e_double_japanese_postcard_rotated,
        e_a6_rotated,
        e_japanese_envelope_kaku_2_rotated,
        e_japanese_envelope_kaku_3_rotated,
        e_japanese_envelope_chou_3_rotated,
        e_japanese_envelope_chou_4_rotated,
        e_b6_jis,
        e_b6_jis_rotated,
        e_12x11,
        e_japanese_envelope_you_4,
        e_japanese_envelope_you_4_rotated,
        e_prc_16k,
        e_prc_32k,
        e_prc_32k_big,
        e_prc_envelop_1,
        e_prc_envelop_2,
        e_prc_envelop_3,
        e_prc_envelop_4,
        e_prc_envelop_5,
        e_prc_envelop_6,
        e_prc_envelop_7,
        e_prc_envelop_8,
        e_prc_envelop_9,
        e_prc_envelop_10,
        e_prc_16k_rotated,
        e_prc_32k_rotated,
        e_prc_32k_big__rotated,
        e_prc_envelop_1_rotated,
        e_prc_envelop_2_rotated,
        e_prc_envelop_3_rotated,
        e_prc_envelop_4_rotated,
        e_prc_envelop_5_rotated,
        e_prc_envelop_6_rotated,
        e_prc_envelop_7_rotated,
        e_prc_envelop_8_rotated,
        e_prc_envelop_9_rotated,
        e_prc_envelop_10_rotated,
    };  
#endif
    
/* restore the original alignment */
#pragma pack()   

#ifdef __cplusplus
}
#endif 

#endif /* H_TRN_CTYPES8 */
