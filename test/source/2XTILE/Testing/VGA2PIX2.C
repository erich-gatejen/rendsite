/*

 VGA2PIX2 utility for the XTile graphics manager

 COPYRIGHT (C) 1992  Erich P Gatejen   All Rights Reserved


 Ver 1.1 : Do not adjust values read for X and Y size	


 File: VGA2PIX2.C    	Written for the Turbo C compiler


 This will convert .VGA bitmaps to .PIX ( Mode X image )
 This differs from VGA2PIX in that it does not adjust the X and Y sizes
    read from the file.  


 MUST BE COMPILED IN LARGE MODEL !!!

*/

#include<stdio.h>
#include<conio.h>
#include<alloc.h>



FILE 	*in,		*out;
char		*inb,     *outb,	/* Base pointers to input and output pic buffers */
		*BufI,	*BufO;	/* Pointers into buffers	*/

char		File_NameI[13];
char		File_NameO[13];     /* File name buffers	*/

unsigned int	Xsize, Ysize, Totalsize;		/* X,Y size and Picture size	*/

int		step, step2, XBlocks;	/* Loop vars and number of XBlocks		*/

main () {

	int	step = 0;

	printf("File name : ");
	scanf("%8s", File_NameI );	/* Get file name from user */

	do {
          File_NameO[ step ] = File_NameI[ step ];
		step++;

	   } while (File_NameI[ step ] != 0);	/* Pack file name */

	File_NameI[ step ] = '.';
	File_NameO[ step ] = '.';
	File_NameI[step+1] = 'V';
	File_NameO[step+1] = 'P';
	File_NameI[step+2] = 'G';
	File_NameO[step+2] = 'I';
	File_NameI[step+3] = 'A';
	File_NameO[step+3] = 'X';
	File_NameI[step+4] = 0;
	File_NameO[step+4] = 0;		/* Add extentions to file names  */


	in  = fopen( File_NameI, "rb" );
	out = fopen( File_NameO, "wb" );	/* open files */

	fread( &Xsize, sizeof( int ), 1, in );
	fread( &Ysize, sizeof( int ), 1, in );  /* Read X,Y size from .VGA file */

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