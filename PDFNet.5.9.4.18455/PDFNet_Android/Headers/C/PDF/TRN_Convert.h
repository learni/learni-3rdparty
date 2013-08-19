//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

#ifndef   H_CCONVERT
#define   H_CCONVERT

#ifdef __cplusplus
extern "C" {
#endif

#include <C/Common/TRN_Types.h>

TRN_API TRN_ConvertFromXps (TRN_PDFDoc in_pdfdoc, TRN_UString in_filename );
TRN_API TRN_ConvertFromXpsMem (TRN_PDFDoc in_pdfdoc, const char* buf, size_t buf_sz );


// EMF conversions are only supported on Windows
TRN_API TRN_ConvertFromEmf (TRN_PDFDoc in_pdfdoc, TRN_UString in_filename );
TRN_API TRN_ConvertPageToEmf (TRN_Page in_page, TRN_UString in_filename );
TRN_API TRN_ConvertDocToEmf (TRN_PDFDoc in_pdfdoc, TRN_UString in_filename );

TRN_API TRN_ConvertPageToSvg (TRN_Page in_page, TRN_UString in_filename);
TRN_API TRN_ConvertPageToSvgWithOptions (TRN_Page in_page, TRN_UString in_filename, TRN_Obj options);
TRN_API TRN_ConvertDocToSvg (TRN_PDFDoc in_pdfdoc, TRN_UString in_filename);
TRN_API TRN_ConvertDocToSvgWithOptions (TRN_PDFDoc in_pdfdoc, TRN_UString in_filename, TRN_Obj options);

TRN_API TRN_ConvertToXps (TRN_PDFDoc in_pdfdoc, TRN_UString in_filename, TRN_Obj options);
TRN_API TRN_ConvertFileToXps (TRN_UString in_inputFilename, TRN_UString in_outputFilename, TRN_Obj options);
TRN_API TRN_ConvertFileToXod (TRN_UString in_filename, TRN_UString out_filename, TRN_Obj options);
TRN_API TRN_ConvertToXod (TRN_PDFDoc in_pdfdoc, TRN_UString out_filename, TRN_Obj options);
TRN_API TRN_ConvertFileToXodStream (TRN_UString in_filename, TRN_Obj options, TRN_Filter* result);
TRN_API TRN_ConvertToXodStream (TRN_PDFDoc in_pdfdoc, TRN_Obj options, TRN_Filter* result);

TRN_API TRN_ConvertToPdf (TRN_PDFDoc in_pdfdoc, TRN_UString in_filename );
TRN_API TRN_ConvertRequiresPrinter (TRN_UString in_filename, TRN_Bool *result );

TRN_API TRN_ConvertPrinterInstall (TRN_UString in_printerName );
TRN_API TRN_ConvertPrinterUninstall ();
TRN_API TRN_ConvertPrinterGetPrinterName (TRN_UString *result);
TRN_API TRN_ConvertPrinterSetPrinterName(TRN_UString in_printerName);
TRN_API TRN_ConvertPrinterIsInstalled (TRN_UString in_printerName, TRN_Bool *result );

TRN_API TRN_ConvertToHtml (TRN_PDFDoc in_pdfdoc, TRN_UString out_path, TRN_Obj options);
TRN_API TRN_ConvertToEpub (TRN_PDFDoc in_pdfdoc, TRN_UString out_path, TRN_Obj html_options, TRN_Obj epub_options);
#ifdef __cplusplus
}
#endif

#endif
