/*

 VGA2PIX utility for the XTile graphics manager

 COPYRIGHT (C) 1992  Erich P Gatejen   All Rights Reserved


 File: VGA2PIX.C    	Written for the Turbo C compiler


 This will convert .VGA bitmaps to .PIX ( Mode X image )


 MUST BE COMPILED IN LARGE MODEL !!!

*/

#include<stdio.h>
#include<conio.h>
#include<string.h>
#include<alloc.h>



FILE 	*in,		*out;
char		*inb,     *outb,	/* Base pointers to input and output pic buffers */
		*BufI,	*BufO;	/* Pointers into buffers	*/

char		File_NameI[80];
char		File_NameO[80];     /* File name buffers	*/

unsigned int	Xsize, Ysize, Totalsize;		/* X,Y size and Picture size	*/

int		step, step2, XBlocks;	/* Loop vars and number of XBlocks		*/

main ( int argn, char* argc[] ) {

	int	step = 0;

	if ( argn < 2 ) {
		puts("  !!ERROR!!  File name not specified.\n");
		exit(1);

	}

	strcpy ( File_NameI, argc[1] );
	strcpy ( File_NameO, argc[1] );

	strcat ( File_NameI, ".VGA" );
	strcat ( File_NameO, ".PIX" );

	in  = fopen( File_NameI, "rb" );
	out = fopen( File_NameO, "wb" );	/* open files */

	if ( (in == NULL)||(out == NULL) ) {
		puts("  !!ERROR!!  File(s) cannot be opened.\n");
		exit(1);

	}

	fread( &Xsize, sizeof( int ), 1, in );
	fread( &Ysize, sizeof( int ), 1, in );  /* Read X,Y size from .VGA file */

	Xsize++;
	Ysize++;	/* Count pixels from 1 instead of 0	*/

	Totalsize = Xsize * Ysize;	/* Calc total pic size in bytes	*/
	XBlocks   = Totalsize / 4;    /* Calc number of XBlocks		*/

	inb  = farmalloc( Totalsize );
	outb = farmalloc( Totalsize ); 	/* Create buffers			*/

	if (( in == NULL ) || ( out == NULL ) || ( outb == NULL)) exit(1);

	fread( inb, sizeof(char), Totalsize, in );   /* Get pic */

	BufO = outb;


	/* Translate and place pic in output buffer			*/
	for ( step = 0; step < 4; step++ ) {

		BufI = inb + step;

		for ( step2 = 0; step2 < XBlocks; step2++ ) {

			*BufO = *BufI;
			BufI  = BufI + 4;	/* Plane at a time, so move every 4th pixel*/
			BufO++;

		};


     }; /* end transfer */


	fwrite( outb, sizeof( char ), Totalsize, out ); /* Write output buffer */

	fclose( in  );
	fclose( out );	/* Close files	*/

	return(0);
};