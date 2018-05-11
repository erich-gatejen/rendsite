/*

 MASKTILE utility for the XTile graphics manager

 COPYRIGHT (C) 1992  Erich P Gatejen   All Rights Reserved


 File: MASKTILE.C    	Written for the Turbo C compiler


 Will create a mask for a .PIX bitmap ( Mode X image )

 Masks will be created two per byte, with first mask in lower nibble */


*/

#include<stdio.h>
#include<conio.h>
#include<alloc.h>


FILE 	*in,		*out;

char		File_NameI[13];          /* Input file name	*/
char		File_NameO[13];          /* Output file name */

unsigned char		*inbuf,        /* Buffer to hold .VGA picture	*/
				*base,         /* Pointer into inbuf			*/
				*current;		/* Current position in inbuf		*/

unsigned char		Hold;          /* Mask generator				*/
unsigned char		Mask;		/* Actual mask					*/

unsigned int		X,
				Y,             /* Inputs for picture size		*/
				Tsize,         /* Size of picture in bytes        */
				Psize;		/* Number of XBlocks in picture    */



int		step, step2;	/* Looping vars	*/

main () {

	printf ("File name : ");
	scanf("%8s", File_NameI );	/* Get file name from user	*/

	printf ("X size : ");
	scanf("%d", &X );			/* Get picture X size		*/

	printf ("Y size : ");
	scanf("%d", &Y );			/* Get picture Y size		*/

	do {
          File_NameO[ step ] = File_NameI[ step ];
		step++;

	   } while (File_NameI[ step ] != 0);    /* Pack the file name	*/

	File_NameI[ step ] = '.';
	File_NameO[ step ] = '.';
	File_NameI[step+1] = 'P';
	File_NameO[step+1] = 'M';
	File_NameI[step+2] = 'I';
	File_NameO[step+2] = 'S';
	File_NameI[step+3] = 'X';
	File_NameO[step+3] = 'K';
	File_NameI[step+4] = 0;
	File_NameO[step+4] = 0;    	/* Add extensions to file names	*/

	Tsize = X * Y;                /* Calculate the picture size		*/
	Psize = ( X / 4 ) * Y;		/* Calc number of XBlocks		*/

	in  = fopen( File_NameI, "rb" );
	out = fopen( File_NameO, "wb" );	/* Open files		*/

     inbuf = farmalloc( Tsize );        /* Allocate memory for picture */

	if (( in == 0 ) || ( out == 0 ) || ( inbuf == NULL)) {
		printf(" ERROR /n");
		exit(1);
		};

	fread( inbuf, sizeof(char), Tsize, in );   /* Get picture */

	base = inbuf;
	current = base;

	/* Loop through the picture and make the mask		*/
     /* If byte is 0, then mask it's bit				*/
	for ( step = 1; step <= Tsize; step = step + 4 ) {

		Hold = 0;    	/* Clear mask holding var		*/

		if (*current != 0) Hold = Hold + 1; /* Plane 0 */

		current = current + Psize;
		if (*current != 0) Hold = Hold + 2; /* Plane 1 */

		current = current + Psize;
		if (*current != 0) Hold = Hold + 4; /* Plane 2 */

		current = current + Psize;
		if (*current != 0) Hold = Hold + 8; /* Plane 3 */

		base++; 	/* Goto next XBlock  */

		current = base;	

		fputc( Hold, out );  	/* Write mask to the file	*/


	};


	fclose( in  );
	fclose( out );	/* Close files and done	*/

	return(0);
};