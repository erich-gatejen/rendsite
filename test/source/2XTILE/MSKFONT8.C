/*

 MSKFONT8 utility for the XTile graphics manager

 COPYRIGHT (C) 1993  Erich P Gatejen   All Rights Reserved


 File: MSKFONT8.C    	Written for the Turbo C compiler


 Will create a mask set for a 8-pixel .FNT font.


*/

#define   CHARSIZE		80	/* size in bytes of each character */ 
#define   Psize		20   /* size in bytes of each plane	*/

#include<stdio.h>
#include<conio.h>
#include<alloc.h>


FILE 	*in,		*out;

char		File_NameI[13];          /* Input file name	*/
char		File_NameO[13];          /* Output file name */

unsigned char		*inbuf,        /* Buffer to hold char picture	*/
				*base,         /* Pointer into inbuf			*/
				*current;		/* Current position in inbuf		*/

unsigned char		Hold;          /* Mask generator				*/

unsigned int		Number;        /* Number of characters			*/



int		step, step2, mainloop;	/* Looping vars	*/

void main () {

	printf ("File name : ");
	scanf("%8s", File_NameI );	/* Get file name from user	*/

	printf ("Number of characters : ");
	scanf("%d", &Number );	    	/* Get number of characters   */

	do {
          File_NameO[ step ] = File_NameI[ step ];
		step++;

	   } while (File_NameI[ step ] != 0);    /* Pack the file name	*/

	File_NameI[ step ] = '.';
	File_NameO[ step ] = '.';
	File_NameI[step+1] = 'F';
	File_NameO[step+1] = 'M';
	File_NameI[step+2] = 'N';
	File_NameO[step+2] = 'S';
	File_NameI[step+3] = 'T';
	File_NameO[step+3] = 'F';
	File_NameI[step+4] = 0;
	File_NameO[step+4] = 0;    	/* Add extensions to file names	*/

	in  = fopen( File_NameI, "rb" );
	out = fopen( File_NameO, "wb" );	/* Open files		*/

	inbuf = farmalloc( CHARSIZE );     /* Allocate memory for each char */

	if (( in == 0 ) || ( out == 0 ) || ( inbuf == NULL)) {
		printf(" ERROR \n");
		exit(1);
		};

	/* Loop through number of characters	   */
	for ( mainloop = 0; mainloop < Number; mainloop++ ) {

	   if ( feof( in ) ) break;	/* See if we have emptied input file  */

	   fread( inbuf, sizeof(char), CHARSIZE, in );   /* Get picture */

	   base = inbuf;
	   current = base;

	   /* Loop through each line and make the mask		*/
	   /* If byte is 0, then mask it's bit				*/
	   for ( step = 0; step < 10; step = step + 1 ) {

		Hold = 0;    	/* Clear mask holding var		*/

		/* Do low nibble of mask */
		if (*current != 0) Hold = Hold + 1; /* Plane 0 */

		current = current + Psize;
		if (*current != 0) Hold = Hold + 2; /* Plane 1 */

		current = current + Psize;
		if (*current != 0) Hold = Hold + 4; /* Plane 2 */

		current = current + Psize;
		if (*current != 0) Hold = Hold + 8; /* Plane 3 */

		current = base;
		current++;          /* Adjust to look for low nibble */

		/* Do high nibble of mask */
		if (*current != 0) Hold = Hold + 16; /* Plane 0 */

		current = current + Psize;
		if (*current != 0) Hold = Hold + 32; /* Plane 1 */

		current = current + Psize;
		if (*current != 0) Hold = Hold + 64; /* Plane 2 */

		current = current + Psize;
		if (*current != 0) Hold = Hold + 128; /* Plane 3 */

		base++;
		base++; 	/* Goto next line  */

		current = base;	

		fputc( Hold, out );  	/* Write mask to the file	*/


	   };

	};

	fclose( in  );
	fclose( out );	/* Close files and done	*/

};