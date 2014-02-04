#ifndef	  H_CPDFNET_H
#define	  H_CPDFNET_H

#ifdef __cplusplus
extern "C" {
#endif 

#include <C/Common/TRN_Types.h>

TRN_API TRN_PDFNetInitialize(const char* license_key);

enum TRN_PDFNetCloudErrorCode
{
   e_PDFNet_STATUS_ERR,
   e_PDFNet_STATUS_OK,
   e_PDFNet_STATUS_NETWORK_ERR,
   e_PDFNet_STATUS_BAD_CREDENTIALS,
   e_PDFNet_STATUS_SERVICE_DOWN,
   e_PDFNet_STATUS_INVALID_OPERATION,
   e_PDFNet_STATUS_NUM
};
	 
TRN_API TRN_PDFNetConnectToCloud(const char* username, const char* password, enum TRN_PDFNetCloudErrorCode* result);
TRN_API TRN_PDFNetConnectToCloudEx(const char* username, const char* password, TRN_Bool demo_mode, enum TRN_PDFNetCloudErrorCode* result);
TRN_API TRN_PDFNetTerminate();
TRN_API TRN_PDFNetSetResourcesPath(TRN_UString path, TRN_Bool* result);
TRN_API TRN_PDFNetGetResourcesPath(TRN_UString* result);

enum TRN_PDFNetCMSType
{
	e_PDFNet_lcms   = 0,		
	e_PDFNet_icm    = 1,		
	e_PDFNet_no_cms = 2
};

TRN_API TRN_PDFNetSetColorManagement(enum TRN_PDFNetCMSType t);
TRN_API TRN_PDFNetSetDefaultDeviceCMYKProfile(const TRN_UString icc_filename);
TRN_API TRN_PDFNetSetDefaultDeviceRGBProfile(const TRN_UString icc_filename);
TRN_API TRN_PDFNetSetDefaultDiskCachingEnabled( TRN_Bool use_disk );
TRN_API TRN_PDFNetSetViewerCache(TRN_Size max_cache_size, TRN_Bool on_disk);

enum TRN_PDFNetCharacterOrdering {
	e_PDFNet_Identity = 0,    
	e_PDFNet_Japan1   = 1,    
	e_PDFNet_Japan2   = 2,    
	e_PDFNet_GB1      = 3,    
	e_PDFNet_CNS1     = 4,    
	e_PDFNet_Korea1   = 5     
};

TRN_API TRN_PDFNetAddFontSubstFromName(const char* fontname, const TRN_UString fontpath, TRN_Bool* result);
TRN_API TRN_PDFNetAddFontSubst(enum TRN_PDFNetCharacterOrdering ordering, const TRN_UString fontpath, TRN_Bool* result);
TRN_API TRN_PDFNetSetTempPath(const TRN_UString temp_path, TRN_Bool* result);
TRN_API TRN_PDFNetGetVersion(double* result);

TRN_API TRN_PDFNetRegisterSecurityHandler(const char* handler_name, const char* gui_name, TRN_PDFNetCreateSecurityHandler factory_method);
TRN_API TRN_PDFNetGetSecHdlrInfoIterator(TRN_Iterator* result);

TRN_API TRN_PDFNetSetNumberWriteProc(char* (*write_proc) (double num, char *in_buf, int in_buf_size));
TRN_API TRN_PDFNetSetNumberReadProc(TRN_Bool (*read_proc) (const TRN_UChar *buf, double *output));


#ifdef __cplusplus
} //extern C
#endif

#endif