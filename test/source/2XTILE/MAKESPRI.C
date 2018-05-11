/*

 MAKESPRI utility for the XTile graphics manager

 COPYRIGHT (C) 1992  Erich P Gatejen   All Rights Reserved


 File: MAKESPRI.C    	Written for the Turbo C compiler


 This will convert .VGA bitmaps to XTile sprite format ( .SPI and .SPM )


 MUST BE COMPILED IN LARGE MODEL !!!

*/

#include<stdio.h>
#include<conio.h>
#include<alloc.h>



FILE 	*in,		*out,
		*Mask,	*Final;
char		*inb,     *outb,	/* Base pointers to input and output pic buffers */
		*BufI,	*BufO;	/* Pointers into buffers	*/

char		*base,    *current; /* Pointers for creating the mask	*/

unsigned char	Hold;		/* Mask hold					*/

char		File_NameI[13];
char		File_NameO[13];
char		File_NameM[13];	/* File name buffers	*/

unsigned int	Xsize, Ysize, Totalsize,
			Newsize;				/* X,Y size and Picture size	*/

unsigned int	AlignSize, PlaneSize;
long			Pos;

int		Align, Astep;				/* Current alignment		*/

int       TossNumber;

int		step, step2, XBlocks,
		NewXBlocks;				/* Loop vars and number of XBlocks	*/

main () {

	step = 0;

	printf("File name : ");
	scanf("%8s", File_NameI );	/* Get file name from user */

	do {
		File_NameO[ step ] = File_NameI[ step ];
		File_NameM[ step ] = File_NameI[ step ];

		step++;

	   } while (File_NameI[ step ] != 0);	/* Pack file name */

	File_NameI[ step ] = '.';
	File_NameO[ step ] = '.';
	File_NameM[ step ] = '.';
	File_NameI[step+1] = 'V';
	File_NameO[step+1] = 'S';
	File_NameM[step+1] = 'S';
	File_NameI[step+2] = 'G';
	File_NameO[step+2] = 'P';
	File_NameM[step+2] = 'P';
	File_NameI[step+3] = 'A';
	File_NameO[step+3] = 'I';
	File_NameM[step+3] = 'M';
	File_NameI[step+4] = 0;
	File_NameO[step+4] = 0;
	File_NameM[step+4] = 0;		/* Add extentions to file names  */


	/* open files */
	in    = fopen( File_NameI, "rb" );
	out   = fopen( "temp.spi", "wb" );
	Final = fopen( File_NameO, "wb" );
	Mask  = fopen( File_NameM, "wb" );


	fread( &Xsize, sizeof( int ), 1, in );
	fread( &Ysize, sizeof( int ), 1, in );  /* Read X,Y size from .VGA file */

	/* Adjust for count from 0 */
	Ysize++;
	Xsize++;

	/* Calc for input picture								*/
	Totalsize = Xsize * Ysize;	/* Calc total pic size in bytes	*/
     XBlocks   = Totalsize / 4;    /* Calc number of XBlocks		*/

     /* Calc for sprite										*/
	Newsize 	 = (Xsize+4) * Ysize;  /* Calc total pic size in bytes	*/
	NewXBlocks = Newsize / 4;        /* Calc number of XBlocks		*/

	inb  = farmalloc( Newsize );
	outb = farmalloc( Newsize );

	if (( in == NULL ) || ( out == NULL ) || ( outb == NULL) ||
	    ( Mask == NULL )) exit(1);


	/* Iterate for each alignment						*/
	for ( Align = 0; Align < 4; Align++ ) {

	   /* Read in picture and add bytes for alignment		*/
	   BufI = inb;
	   BufO = outb;
	   for ( step = 0; step < Ysize; step++ ) { /* loop through each line */
		 
		 /* Add line's leading bytes	*/
		 Astep = Align;
		 while ( Astep > 0 ) {
		    *BufI =  0;
		    BufI++;
		    Astep--;
		 }; /* end while */

		 /* Read data from file */
		 fread( BufI, sizeof( char ), Xsize, in );
		 
		 /* Point to end of data just read */
		 BufI = BufI + Xsize;

		 /* Add line's trailing bytes */
		 Astep = 4 - Align;
		 while ( Astep > 0 ) {
		    *BufI =  0;
		    BufI++;
		    Astep--;
		 }; /* end while */

	   }; /* end for */

	   /* Reset input file for next iteration			    */
	   rewind( in );

	   /* If this is not the last alignment, read the size in again and toss */
	   if ( Align < 3 ) {
		 fread( &TossNumber, sizeof( int ), 1, in );
		 fread( &TossNumber, sizeof( int ), 1, in ); 
	   };

	   /* Translate and place pic in output buffer			*/
	   for ( step = 0; step < 4; step++ ) {

	        BufI = inb + step;

		   for ( step2 = 0; step2 < NewXBlocks; step2++ ) {

			   *BufO = *BufI;
                  /* Plane at a time, so move every 4th pixel*/
			   BufI  = BufI + 4;	
			   BufO++;

		   };

        }; /* end transfer */

	   /* Write picture buffer  */
	   fwrite( outb, sizeof( char ), Newsize, out ); 


	 /* -- Make Mask -- */
	   
	   /* Point into picture */
	   base    = outb;
	   current = base;

	   /* Loop through the picture and make the mask		*/
        /* If byte is 0, then mask it's bit				*/
	   for ( step = 1; step <= Newsize; step = step + 4 ) {

		   Hold = 0;    	/* Clear mask holding var		*/

		   if (*current != 0) Hold = Hold + 1; /* Plane 0 */

		   current = current + NewXBlocks;
		   if (*current != 0) Hold = Hold + 2; /* Plane 1 */

		   current = current + NewXBlocks;
		   if (*current != 0) Hold = Hold + 4; /* Plane 2 */

		   current = current + NewXBlocks;
		   if (*current != 0) Hold = Hold + 8; /* Plane 3 */

		   base++; 	/* Goto next XBlock  */

		   current = base;

		   fputc( Hold, Mask );  	/* Write mask to the file	*/

	   }; /* end for */


	}; /* end for loop */

	fclose( in   );
	fclose( out  );
	fclose( Mask );  /* Close files	*/

	/* Put like alignments back to back */
	in   = fopen( "temp.spi", "rb" );		/* Reopen temp file for read */

	PlaneSize = Newsize/4;	/* Size of an alignment div by 4. */

	/* Loop for each plane */
	for (step = 0; step < 4; step++ ) {

	   /* First alignments plane */         /* SEEK_SET */
	   Pos 	= PlaneSize * step;
	   fseek ( in, Pos, SEEK_SET );
	   fread( inb, 1, PlaneSize, in );
	   fwrite(inb, 1, PlaneSize, Final);

	   /* Second alignments plane */         /* SEEK_SET */
	   Pos 	= Pos + Newsize;
	   fseek ( in, Pos, SEEK_SET );
	   fread( inb, 1, PlaneSize, in );
	   fwrite(inb, 1, PlaneSize, Final);

	   /* Third alignments plane */         /* SEEK_SET */
	   Pos 	= Pos + Newsize;
	   fseek ( in, Pos, SEEK_SET );
	   fread( inb, 1, PlaneSize, in );
	   fwrite(inb, 1, PlaneSize, Final);

	   /* Forth alignments plane */         /* SEEK_SET */
	   Pos 	= Pos + Newsize;
	   fseek ( in, Pos, SEEK_SET );
	   fread( inb, 1, PlaneSize, in );
	   fwrite(inb, 1, PlaneSize, Final);

	}; /* end plane loop */

	/* Close last two files and kill temp */
	fclose( in );
	fclose( Final );
	unlink("temp.spi");

	/* Report new size */
	printf("New size for sprite : %d X  by  %d Y.", Xsize + 4, Ysize );

	/* Free pointers */
	farfree(inb);
	farfree(outb);
	farfree(BufI);
	farfree(BufO);
	farfree(base);
	farfree(current);

	return(0);

};