/*
 * General constants.
 * Erich - Sep 2007
 */
package rendsite;

import things.common.ThingsCodes;

/**
 * Message codes.
 * @author  erich
 * <i>Version History</i>
 * <pre>
 * 10NOV09 - EPG - First package release.
 * </pre>
 */
public interface Codes extends ThingsCodes {
	
	// ====================================================================================================
	// == PANIC ===========================================================================================
	// This will end all processing for the entire run.
	// USER_PANIC_RESERVED_END = 0x06FF;
	// PANIC_TOP = 0x0FFF;	
	
	public final static int PANIC_SETUP = 0x0700;
	public final static int PANIC_SETUP__FRAME_TIME = 0x0710;
	
	public final static int PANIC_SETUP_RESOURCES = 0x0720;
	public final static int PANIC_SETUP_RESOURCES__COULD_NOT_LOAD = 0x0721;
	public final static int PANIC_SETUP_RESOURCES__COULD_NOT_LOAD_MESSAGES_FOR_LOCALE = 0x0730;
	public final static int PANIC_SETUP_RESOURCES__COULD_NOT_BUILD_MESSAGING_OBJ = 0x0735;
	
	public final static int PANIC_RUN = 0x0800;
	public final static int PANIC_RUN_SPURIOUS = 0x0801;
	public final static int PANIC_RUN_SCOPE_RUINED = 0x0802;
	
	public final static int PANIC_PROCESSING_DIRECTORY = 0x0900;
	public final static int PANIC_PROCESSING_DIRECTORY__EXCEEDED_ALLOWABLE = 0x0910;	// 
	public final static int PANIC_PROCESSING_DIRECTORY__EXCEEDED_ALLOWABLE_GLOBAL = 0x0911;	//
	public final static int PANIC_PROCESSING_FILE = 0x0950;
	public final static int PANIC_PROCESSING_FILE__EXCEEDED_ALLOWABLE = 0x0960;	// 
	public final static int PANIC_PROCESSING_FILE__EXCEEDED_ALLOWABLE_GLOBAL = 0x0961;	// 
	
	public final static int PANIC_RENDER = 0x0A00;
	public final static int PANIC_RENDERER_INSTANTIATION = 0x0A01;
	public final static int PANIC_RENDERER_INST__COUNT_NOT_FIND_TOO_MANY_TIMES  = 0x0A02;
	
	public final static int PANIC_RENDERER__PROCESS_FILE = 0x0A51;			// SPECIAL: Comes out of the PROCESSOR 
	public final static int PANIC_RENDERER__CHECK_FILE = 0x0A52;			// SPECIAL: Comes out of the PROCESSOR 
	public final static int PANIC_RENDERER__PROCESS_DIRECTORY = 0x0A53;		// SPECIAL: Comes out of the PROCESSOR 
	public final static int PANIC_RENDERER__CATALOG = 0x0A54;				// SPECIAL: Comes out of the PROCESSOR

	public final static int PANIC_CHANGE = 0x0B00;	
	public final static int PANIC_CHANGE_FILE = 0x0B01;		
	public final static int PANIC_CHANGE_FILE__CANNOT_WRITE = 0x0B02;	// "Cannot write to change file."
	public final static int PANIC_CHANGE_FILE__CANNOT_ACCESS = 0x0B03;	// "Cannot access change file."
	
	public final static int PANIC_FILE_SPURIOUS = 0x0FFE;
	public final static int PANIC_BUG = 0x0FFF;

	// ====================================================================================================
	// == FAULT ===========================================================================================
	// This will end all processing for a directory.
	// USER_FAULT_RESERVED_END = 0x30FF;
	// FAULT_TOP = 0x3FFF;
	
	// -- SETUP
	public final static int FAULT_SETUP = 0x3100;
	
	public final static int FAULT_ENGINE_SETUP = 0x3120;
	public final static int FAULT_ENGINE_SETUP_SCOPE_STACK = 0x3121;
	
	public final static int FAULT_CONTEXT = 0x3200;
	public final static int FAULT_CONTEXT_CONFIGURATION = 0x3201;
	public final static int FAULT_CONTEXT_MISSING_REQUIRED_PROP = 0x3210;
	public final static int FAULT_CONTEXT_MISSING_REQUIRED_PROP__TEMPLATE_PATH = 0x3211;
	public final static int FAULT_CONTEXT_DIRECTORY = 0x32A0;
	public final static int FAULT_CONTEXT_DIRECTORY__SOURCE_DOESNT_EXIST = 0x32A1;
	public final static int FAULT_CONTEXT_DIRECTORY__OUTPUT_DOESNT_EXIST = 0x32A2;
	
	
	public final static int FAULT_METAFILE = 0x3145;
	public final static int FAULT_METAFILE_LOAD = 0x3146;
	public final static int FAULT_METAFILE_NOT_FOUND = 0x3147;
	
	public final static int FAULT_METAFILE__FILE_CONFIGD_BUT_NOT_PRESENT = 0x3180;
	public final static int FAULT_METAFILE__DIR_CONFIGD_BUT_NOT_PRESENT = 0x3181;
	
	// -- FILES
	public final static int FAULT_IN_DIRECTORY = 0x3301;
	public final static int FAULT_IN_DIRECTORY_FILES = 0x3310;
	public final static int FAULT_IN_DIRECTORY_DIRECTORIES = 0x3311;
	public final static int FAULT_IN_FILE = 0x3340;
	public final static int FAULT_IN_FILE_COULD_NOT_OPEN = 0x3351;
	public final static int FAULT_IN_FILE_COULD_GET_LAST_MODIFIED = 0x3352;
	
	public final static int FAULT_NEIGHBOR = 0x3370;
	public final static int FAULT_NEIGHBOR_ALREADY_DEFINED = 0x3371;
	public final static int FAULT_NEIGHBOR_NOT_DEFINED = 0x3372;
	public final static int FAULT_NEIGHBOR_OPENED_FOR_READ = 0x3373;
	
	// -- ENGINE
	public final static int FAULT_ENGINE = 0x3400;
	public final static int FAULT_ENGINE_FRAME = 0x3401;
	
	public final static int FAULT_TEMPLATE = 0x3450;
	public final static int FAULT_TEMPLATE_LOAD = 0x3451;
	public final static int FAULT_TEMPLATE_LOAD__FILE_NOT_FOUND = 0x3452;
	public final static int FAULT_TEMPLATE_LOAD__FILE_COULD_NOT_READ = 0x3453;

	// -- RENDERER
	
	public final static int FAULT_RENDERER = 0x3700;
	public final static int FAULT_RENDERER_INSTANTIATION = 0x3701;
	public final static int FAULT_RENDERER_INST__COUNT_NOT_FIND = 0x3702;
	
	public final static int FAULT_RENDERER_COPY = 0x3710;
	
	public final static int FAULT_RENDERER__PROCESS_FILE = 0x37F1;			// SPECIAL: Comes out of the PROCESSOR 
	public final static int FAULT_RENDERER__CHECK_FILE = 0x37F2;			// SPECIAL: Comes out of the PROCESSOR 
	public final static int FAULT_RENDERER__PROCESS_DIRECTORY = 0x37F3;		// SPECIAL: Comes out of the PROCESSOR 
	public final static int FAULT_RENDERER__CATALOG = 0x37F4;				// SPECIAL: Comes out of the PROCESSOR
	
	// -- CHANGE
	public final static int FAULT_CHANGE = 0x3800;
	public final static int FAULT_CHANGE_CONTEXT = 0x3801;
	public final static int FAULT_CHANGE_CONTEXT_RESET = 0x3802;		// x
	
	public final static int FAULT_NOTIFICATION = 0x3880;
	public final static int FAULT_NOTIFICATION_SETUP = 0x3881;
	public final static int FAULT_NOTIFICATION_DISPOSE = 0x3882;
	
	// ====================================================================================================
	// == ERROR ===========================================================================================
	// This will end all processing for a file or catalog.
	// USER_ERROR_RESERVED_END = 0x61FF;
	// ERROR_TOP = 0x6FFF
	
	// -- SETUP
	public final static int ERROR_SETUP = 0x6200;
	
	// -- CONFIG
	public final static int ERROR_CONFIGURATION = 0x6250;
	public final static int ERROR_CONFIG__FILTER = 0x6255;
	
	// -- FILES
	public final static int ERROR_IN_DIRECTORY = 0x6300;
	public final static int ERROR_IN_DIRECTORY__NOT_IN_SOURCE = 0x6301;
	public final static int ERROR_IN_DIRECTORY__GET_SUBS = 0x6302;
	public final static int ERROR_IN_DIRECTORY__GET_FILES = 0x6303;
	public final static int ERROR_IN_DIRECTORY__GET_METAFILES = 0x6304;
	
	public final static int ERROR_CANNOT_MAKE_SUBDIRECTORY = 0x6320;
	public final static int ERROR_CANNOT_CLEAN_SUBDIRECTORY = 0x6321;

	public final static int ERROR_IN_FILE = 0x6350;
	public final static int ERROR_IN_FILE_COULD_NOT_OPEN = 0x6351;
	public final static int ERROR_IN_FILE_COULD_NOT_OPEN__CATALOG = 0x6352;
	
	public final static int ERROR_IN_FILE_NOT_COPY = 0x6370;
	public final static int ERROR_IN_FILE_COULD_NOT_ASSERT = 0x6380;
	public final static int ERROR_IN_FILE_COULD_NOT_ASSERT__NEIGHBOR = 0x6381;
	public final static int ERROR_IN_FILE__PATH_NOT_IN_SOURCE = 0x6390;
	
	// -- ENGINE	
	public final static int ERROR_RUN = 0x6400;			// Overall, the run ended with errors.
	public final static int ERROR_RUN__ERRORS_REPEATED = 0x6401;			// Overall, the run ended with errors.
	
	public final static int ERROR_FRAME = 0x6450;
	public final static int ERROR_FRAME_INITIALIZATION = 0x6451;
	
	// -- RENDERER
	public final static int ERROR_RENDERER = 0x6600;
	public final static int ERROR_RENDERER_INST__DOES_NOT_EXIST = 0x6610;
	
	public final static int ERROR_RENDERER_CHECK = 0x6650;
	public final static int ERROR_RENDERER_CHECK__NEIGHBOR = 0x6651;
	
	public final static int ERROR_RENDERER_CATALOG = 0x6700;
	public final static int ERROR_RENDERER_CATALOG__WRITE_ERROR = 0x6701;
	public final static int ERROR_RENDERER_FILE = 0x6750;
	public final static int ERROR_RENDERER_FILE__WRITE_ERROR = 0x6751;
	public final static int ERROR_RENDERER_FILE_NEIGHBOR = 0x6760;
	public final static int ERROR_RENDERER_FILE_NEIGHBOR__WRITE_ERROR = 0x6761;
	
	// -- CHANGE
	
	// ====================================================================================================
	// == WARNING  ===========================================================================================
	// USER_WARNING_RESERVED_END = 0x90FF;
	// WARNING_TOP = 0x9FFF
	
	// -- SETUP
	
	// -- FILES

	// -- ENGINE	
	
	// -- RENDERER

	// -- CHANGE
	
	// ====================================================================================================
	// == INFO  ===========================================================================================
	// USER_INFO_RESERVED_END = 0xC0FF;
	// INFO_TOP = 0xCFFF
	
	// -- SETUP
	
	// -- FILES
	public final static int INFO_FILE = 0xC200;
	public final static int INFO_FILE_UPDATED = 0xC201;
	public final static int INFO_FILE_NO_UPDATE_NEEDED = 0xC202;
	public final static int INFO_CATALOG_UPDATED = 0xC211;
	public final static int INFO_CATALOG_NO_UPDATE_NEEDED = 0xC212;

	// -- ENGINE	
	public final static int INFO_COMPLETED = 0xC300;
	public final static int INFO_COMPLETED_NO_ERRORS = 0xC301;
	
	// -- RENDERER

	// -- CHANGE
	

	// ====================================================================================================
	// == DEBUG ===========================================================================================
	// USER_DEBUG_RESERVED_END = 0xF0FF;
	// DEBUG_TOP = 0xFFFE
	
	// -- SETUP
	
	// -- FILES
	public final static int DEBUG_FILE = 0xF200;
	public final static int DEBUG_FILE_UPDATE_NEEDED = 0xF210;
	public final static int DEBUG_FILE_DELETED = 0xF212;

	// -- ENGINE	
	
	// -- RENDERER

	// -- CHANGE


	
}



