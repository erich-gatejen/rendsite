/*

	XTile Combine Files utility

	COPYRIGHT (C) 1993 Erich P  Gatejen

*/
#include<stdlib.h>
#include<stdio.h>
#include<ctype.h>
#include<string.h>


/* ------ Globals -------------- */
   FILE *OutFile;
   FILE *ScriptFile;
   FILE *ReportFile;

   unsigned long  Start = 0;
   char	Buffer[81];	/* Script line buffer */
   char DefaultExt[81];


   void  DoFile(void);

/* ------ Functions ------------ */
void  DoFile( ) {

   FILE  *InFile;
   char   Name[81];	/* Extra room for errors */
   int	  Step = 0;
   long   Size;
   unsigned char	Byte;
   int    EXTFlag	=	0;


  /* Move name into Name buffer, up until whitespace */
  while ( !isspace(Buffer[Step]) ) {

     Name[Step] = Buffer[Step];
     if ( Name[Step] == '.' ) EXTFlag = 1; /* Set extension present flag */
     ++Step;
  }

  /* Pad NULL */
  Name[Step] = NULL;

  /* Add the default extension if none provided */
  if ( !EXTFlag ) strcat( Name, DefaultExt );


  /* Open the file */
   if ( (InFile = fopen( Name, "rb" )) == NULL ) {
      puts("\n\rCannot open file : ");
      puts(Name);
      puts(" in script!  Combine aborted.\n\rCombine file is corrupt!\n\r");
      fputs("!!!!!Combine corrupted!!!!!", ReportFile );
      exit(1);
   };

  /* Put report data */
  fprintf(ReportFile, "Item %12s starts at : %08li.\n", Name, Start );

  /* Copy Data */

  fread ( &Byte, sizeof( unsigned char ), 1, InFile );
  while ( !feof( InFile ) ) {

     fwrite( &Byte, sizeof( unsigned char ), 1, OutFile );
     ++Start;
     fread ( &Byte, sizeof( unsigned char ), 1, InFile );
  }
  fclose( InFile );

}


/* ------ Main ----------------- */
void  main( int argn, char* argc[] ) {

   if ( argn == 1 ) {

     puts("\n\r...  XCOMBINE tool for the XTILE Graphics System  ...");
     puts    ("...       Copyright (C) Erich P Gatejen 1993      ...");
     puts("- Arguments : ");
     puts("  (1) Name of the script file.");
     puts("  (2) Name of the combine and report file.");
     puts("  (3) [Optional] Default extension (without this, it is .PIX). \n");
     exit(0);
   }

   /* is there right number of arg */
   if ( (argn != 3)&&(argn != 3)) {
      puts("\n\rBad number of arguments! -- !Program terminated\n\r");
      return;
   };

   /* Set Default extension.  If none is supplied it is ".PIX" */
   if ( argn == 3 ) strcpy( DefaultExt, ".PIX");
   else strcpy( DefaultExt, argc[3]);


  /* -------  Open script and output files ------ */

  /* Build script file name and open */
   strcpy( Buffer, argc[2] );
   strcat( Buffer, ".SCR"  );
   if ( (ScriptFile = fopen( Buffer, "rt" )) == NULL ) {
      puts("\n\rCannot open script file! -- !Program terminated\n\n\r");
      return;
   };

  /* Build report file name and open */
   strcpy( Buffer, argc[1] );
   strcat( Buffer, ".RPT"  );
   if ( (ReportFile = fopen( Buffer, "wt" )) == NULL ) {
      puts("\n\rCannot open report file! -- !Program terminated\n\n\r");
      return;
   };

   strcpy( Buffer, argc[1] );
   strcat( Buffer, ".CMB"  );
   if ( (OutFile = fopen( Buffer, "wb" )) == NULL ) {
      puts("\n\rCannot open combine file! -- !Program terminated\n\n\r");
      return;
   };


  /* -------  Process script file ----------- */

  /* Put header on report file */
   fputs( "---- Combine Report ------\n\r", ReportFile);
   fputs( " For combine file : ", ReportFile );
   fputs( Buffer, ReportFile );
   fputs( "\n\r", ReportFile );

  /* While not eof get script lines and process */
   while( !feof(ScriptFile) ) {

     fgets( Buffer, 81, ScriptFile );

     /* It may be a comment line(ignore), if not, process it */
     if ( (Buffer[0] != '/') && (!isspace(Buffer[0])) ) DoFile();

  } /* end while */

  /* ------  Clean up ----------------------- */

  /* Put report file tail */
  fputs("\n\r  Total file size : ", ReportFile );
  fprintf(ReportFile, "%08li. \n\r\n", Start );

  /* Close all files */
  fcloseall();

  /* Report combine done! */
  puts("....Combine done.  !!Successful!!\n");

}
