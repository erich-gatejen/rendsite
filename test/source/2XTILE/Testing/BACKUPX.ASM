; XTile  Assembler routine lib
;
; COPYRIGHT (C) 1993 Erich P Gatejen 1993  All Rights Reserved
;
; File: XTILE.ASM
;
; Mode X graphics manager.

; Ver 1.1 : Add sprites
;           Add 4-Pixel font   
;		  Add mouse interface		
;           Add put pixel

; Ver 2.0 : Major rewrite!
;		  Add virtual size
;		  Split-screen
;           More modes


IDEAL


INCLUDE "XTILE20!.USE"


DOSSEG

; LARGE memory model.
			MODEL 	LARGE

; ------------------------------------------------------------------------
; ----------------------------- Data Seg ---------------------------------
; ------------------------------------------------------------------------
			FARDATA

; --- Mode set tables ----------------------------------------------------

; - CRTC Values
CRTC_Single_Scan:       ; Single scan per line (400/480 Line modes)
	   dw  04009H      ; 1 Scan Line
	   dw  00014H      ; Dword Mode off
	   dw  0E317H      ; Byte Mode

CRTC_Double_Scan:       ; Double scan per line (200/240 Line modes)
	   dw  04109H      ; Cell Height (2 Scan Lines)
	   dw  00014H      ; Dword Mode off
	   dw  0E317H      ; turn on Byte Mode

CRTC_320_Wide:          ; CRTC Setup Data for 320 Horz Pixels
	   dw  05F00H      ; Horz total
	   dw  04F01H      ; Horz Displayed
	   dw  05002H      ; Start Horz Blanking
	   dw  08203H      ; End Horz Blanking
	   dw  05404H      ; Start H Sync
	   dw  08005H      ; End H Sync
 
CRTC_360_Wide:          ; CRTC Setup Data for 360 Horz Pixels
	   dw  06B00H      ; Horz total
	   dw  05901H      ; Horz Displayed
	   dw  05A02H      ; Start Horz Blanking
	   dw  08E03H      ; End Horz Blanking
	   dw  05E04H      ; Start H Sync
	   dw  08A05H      ; End H Sync

CRTC_200_Tall:
CRTC_400_Tall:          ; CRTC Setup Data for 200/400 Line modes
	   dw  0BF06H      ; Vertical Total
	   dw  01F07H      ; Overflow
	   dw  09C10H      ; V Sync Start
	   dw  08E11H      ; V Sync End/Prot Cr0 Cr7
	   dw  08F12H      ; Vertical Displayed
	   dw  09615H      ; V Blank Start
	   dw  0B916H      ; V Blank End
	   dw  0           ; End table

CRTC_240_Tall:
CRTC_480_Tall:          ; CRTC Setup Data for 240/480 Line modes
	   dw  00D06H      ; Vertical Total
	   dw  03E07H      ; Overflow
	   dw  0EA10H      ; V Sync Start
	   dw  08C11H      ; V Sync End/Prot Cr0 Cr7
	   dw  0DF12H      ; Vertical Displayed
	   dw  0E715H      ; V Blank Start
	   dw  00616H      ; V Blank End
	   dw  0           ; End table


; - Tables for each mode
XMODE_320x240:          ; 320 by 240 Pixels | Standard ModeX
	dw  0E3h        ; 480 scan Lines & 28 Mhz Clock
	dw  320, 240    ; View size 
	dw  OFFSET CRTC_320_Wide 
	dw  OFFSET CRTC_240_Tall
	dw  OFFSET CRTC_Double_Scan

XMODE_320x200:          ; 320 by 200 mode
	dw  063h        ; 400 scan Lines & 25 Mhz Clock
	dw  320, 200    ; View size 
	dw  OFFSET CRTC_320_Wide 
	dw  OFFSET CRTC_200_Tall
	dw  OFFSET CRTC_Double_Scan

XMODE_320x400:          ; 320 by 200 Pixels
	dw  063h        ; 400 scan Lines & 25 Mhz Clock
	dw  320, 400    ; View size 
	dw  OFFSET CRTC_320_Wide
	dw  OFFSET CRTC_400_Tall
	dw  OFFSET CRTC_Single_Scan
 
XMODE_360x240:          ; 360 by 240 Pixels
	dw  0E7h        ; 480 scan Lines & 28 Mhz Clock
	dw  360, 240    ; View size 
	dw  OFFSET CRTC_360_Wide
	dw  OFFSET CRTC_240_Tall
	dw  OFFSET CRTC_Double_Scan

XMODE_360x480:          ; 360 by 480 Pixels
	dw  0E7h        ; 480 scan Lines & 28 Mhz Clock
	dw  360, 480    ; View size 
	dw  OFFSET CRTC_360_Wide 
	dw  OFFSET CRTC_480_Tall
	dw  OFFSET CRTC_Single_Scan
  
XMODE_320x480:          ; 320 by 480 Pixels
	dw  0E3h        ; 480 scan Lines & 28 Mhz Clock
	dw  320, 480    ; View size 
	dw  OFFSET CRTC_320_Wide 
	dw  OFFSET CRTC_480_Tall
	dw  OFFSET CRTC_Single_Scan

XMODE_360x200:          ; 360 by 200 Pixels
	dw  067h        ; 400 scan Lines & 28 Mhz Clock
	dw  360, 200    ; View size
	dw  OFFSET CRTC_360_Wide 
	dw  OFFSET CRTC_200_Tall
	dw  OFFSET CRTC_Double_Scan

XMODE_360x400:          ; 360 by 200 Pixels
	dw  067h        ; 400 scan Lines & 28 Mhz Clock
	dw  360, 400    ; View size 
	dw  OFFSET CRTC_360_Wide 
	dw  OFFSET CRTC_400_Tall
	dw  OFFSET CRTC_Single_Scan

; Structure to access mode tables
STRUC MODE_TABLE   
     MiscTiming    dw  ?
     XSizeM	   dw  ?
     YSizeM	   dw  ?
     WidthDataO	   dw  ?
	HightDataO	   dw  ?
     ScanDataO	   dw  ?
										
ENDS MODE_TABLE

 
; - Table of available modes.  Position relates to mode number
XMODE_TABLE:
	dw  OFFSET 	XMODE_320x240		; Mode 0.  Standard mode X
	dw  OFFSET 	XMODE_320x200		; Mode 1.
	dw  OFFSET 	XMODE_320x400		; Mode 2.
	dw  OFFSET 	XMODE_360x400		; Mode 3.
	dw  OFFSET 	XMODE_360x200		; Mode 4.
	dw  OFFSET 	XMODE_360x240		; Mode 5.
	dw  OFFSET 	XMODE_320x480		; Mode 6.
	dw  OFFSET 	XMODE_360x480		; Mode 7.


; --- Other Tables --------------------------------------------------------

; Pixel mask table for pixel put.
LABEL	PMask_Table	BYTE
	db		00000001b ; Plane 0, first pix of XBlock
	db		00000010b ; Plane 1, second pix of XBlock
	db		00000100b ; Plane 2, third pix of XBlock
	db		00001000b ; Plane 3, forth pix of XBlock


; ---- Copyright notice --------------------------------------------------
Banner  db	   'COPYRIGHT (C) 1993 Erich P Gatejen' ; Do not remove
	   db        ' All Rights Reserved '              ; Do not remove
ID	   db	   ' !!!!'


; ---- XTile local data -------------------------------------------------- 

; --- Define the current write page
Write_Page	dw	?	; Offset into current write page
WLine_Offset	dw	?	; Line size in the current write page

; --- Define the current display page
Display_Page   dw	?	; Offset into display page
DLine_Offset	dw	?	; Line size in current display page
DisplaySizeX	dw	?	; Size of display page (X)
DisplaySizeY	dw	?	; Size of display page (Y)
ViewXLoc   	dw	?	; Starting X of view
ViewYLoc  	dw	?	; Starting Y of view
MaxViewX  	dw	?	; Maximum View X
MaxViewY  	dw   ?	; Max     View Y
SplitY		dw	?	; 0 = no split screen.  Else, Y size.
ScreenSizeX	dw   ?	; Actual screen size
ScreenSizeY    dw	?	; Actual screen size


; --- Font data
; 8-pix font
UpLoaded8		dw	1	; Is it uploaded?  Assume not.
Font_SiteU8	dw	?	; Offset of registered uploaded font
LABEL Font_Addr8	DWORD  ; For loading the address of an non-uploaded font
Font_SiteD8	dw	?	; Offset of registered not-uploaded font
FSS8			dw	?	; Segment.  Don't change
Char_Base8	dw	?	; Base character for font
Font_Mask_S8	dw	?	; Seg with masks for font set
Font_Mask_O8	dw	?	; Offset

; 4-pix font
UpLoaded4		dw	1	; Is it uploaded?  Assume not.
Font_SiteU4	dw	?	; Offset of registered uploaded font
LABEL Font_Addr4	DWORD  ; For loading the address of an non-uploaded font
Font_SiteD4	dw	?	; Offset of registered not-uploaded font
FSS4			dw	?	; Segment.  Don't change
Char_Base4	dw	?	; Base character for font
Font_Mask_S4	dw	?	; Seg with masks for font set
Font_Mask_O4	dw	?	; Offset


; --- Mouse data
Mouse_Hndlr_O  dd  FAR	?    ; Address of the user defined mouse handler
LABEL MPointerMask DWORD      ; Address of the S:O for the next  two
MPointerMask_O dw	?    ; Location in user data of the mouse pntr mask
MPointerMask_S dw	?    ; Segment ^
MUEventMask    dw	?    ; Events user wishes to be reported
MLastX	     dw	?    ; Last known X location of the mouse
MLastY         dw   ?    ; Last known Y location of the mouse
LockHandler	dw	0	; Block re-entry into mouse handler

; --- Clip Values
ClipXS		dw	?	; Clip starting X
ClipYS		dw	?	; Clip starting Y
ClipXE		dw	?	; Clip ending	 X
ClipYE		dw	?	; Clip ending	 Y



ENDS



; ------------------------------------------------------------------------
; ---------------------------- Code Seg ----------------------------------
; ------------------------------------------------------------------------
			SEGMENT 	CODE	   WORD	PUBLIC  'CODE'


			ASSUME	cs:CODE

; --------------------------- _XInit_Mode -------------------------------
; - This will initialize Mode X.
; -
public	  _XInit_Mode

PROC	  _XInit_Mode   FAR

	ARG	Mode:WORD

	  push		bp
	  mov		bp,   sp    ; Save Stack frame
	  push         si di ds    ; Save calling frame

	  ASSUME   ds:  @fardata
	  mov	 ax,  @fardata    ; Set DS to segment w/ Table
	  mov	 ds,  ax

	  ; Load SI with pointer to table entry
	  mov	   bx,	[Mode]	         ; Get mode number
	  shl	   bx,  1	         ; Find table offset
		; FIX THIS
	  mov	   di,	OFFSET XMODE_TABLE ; Point to the entry
	  add        di,  bx
	  mov	   si,  [di]

	  ; Set mode
	  mov      ax,  13H         ; First, setup as Mode 13H
	  int      10h              ; Let BIOS do it
 
	  mov	   dx,  SC_INDEX    ; Modify the Sequence Controller
	  mov	   ax,	0604h	    ; Disable CHAIN-4
	  out	   dx,	ax
	  				
	  mov	   ax,	0100h	    ; Synch reset 
	  out	   dx,	ax			

	  mov	   dx,	MISC_OUTPUT 	; Modify the Misc Output
	  mov	   ax,	[(MODE_TABLE PTR si).MiscTiming] ; Set timing and size
	  out	   dx,	al

	  mov	   dx,	SC_INDEX	; Modify the Sequence Cont.
	  mov	   ax,  0300h		; Resart the sequencer
	  out	   dx,  ax
		
       ; Unprotect the CRTC registers
	  mov	   dx,	CRTC_INDEX
	  mov	   al,  011h
	  out	   dx,  al
	  inc 	   dx
	  in	   al,	dx
	  and      al,  07Fh	; Mask out protect bit
	  out	   dx,  al	; Unprotect

    ; --- Set up the CRT controller
	  mov	   dx,  CRTC_INDEX
	  
	  ; - Set width
	  mov	   cx,  6
	  mov      di,  [(MODE_TABLE PTR si).WidthDataO] ;Get width table off
   @@LoopWidth:
	  mov	   ax,  [di]		; Load ax with table vaule
	  out	   dx,  ax
	  add	   di,  2		; Point to next item
	  loop	   @@LoopWidth
	  
       ; - Set hight	 	   	  					
	  mov	   di,  [(MODE_TABLE PTR si).HightDataO] ;Get hight table off
   @@LoopHight:
	  mov	   ax,  [di]
	  or	   ax,  ax		; Is it the end of table
	  jz	   @@Scans		; Yes, move on		
	  add	   di,  2		; Point to next item
	  out	   dx,  ax		; Send current one out
	  jmp	   @@LoopHight
	  
       ; - Set scans
   @@Scans:
	  mov	   cx,  3
	  mov      di,  [(MODE_TABLE PTR si).ScanDataO] ; Get width table offset
   @@LoopScans:
	  mov	   ax,  [di]		; Load ax with table vaule
	  out	   dx,  ax
	  add	   di,  2		; Point to next item
	  loop	   @@LoopScans

    ; --- Set default page information

	  ; - Write page
	  mov	ax,           0
	  mov   [Write_Page], ax	   ; Write page offset 0000
	  mov   ax,	     [(MODE_TABLE PTR si).XSizeM]  ; Default XBlocks p/line is X size
	  shr   ax, 1
	  shr   ax, 1
	  mov   [WLine_Offset], ax	   ; Set line size in XBlocks

	  ; - Display Page.  Same page as the default writepage
	  mov   [DLine_Offset], ax	   ; Set line size in XBlocks
	  mov   ax,		0
	  mov   [Display_Page], ax	   ; Display offset 0000
	  mov   [SplitY],	ax	   ; No split screen
	  mov   [ViewX],	ax	   ; View at base
	  mov   [ViewY],	ax	   ; View at base
	  mov   [MaxViewX],	ax	   ; No room to move
	  mov   [MaxViewY],	ax
	  mov   ax,	         [(MODE_TABLE PTR si).XSizeM] ; Display size X
	  mov   [DisplaySizeX], ax
	  mov   [ScreenSizeX],  ax
	  mov   ax,	         [(MODE_TABLE PTR si).YSizeM] ; Display size Y
	  mov   [DisplaySizeY], ax
	  mov   [ScreenSizeY],  ax


	  ; !!DONE!!
	  pop	  ds di si             ; Return state
	  pop	  bp
	  ret



ENDP			_XInit_Mode


; --------------------------- _XSet_Write_Page ------------------------------
; - This will set a write page.  It will be the current write page for 
; - most write operations ( the ones that don't use this page will say
; - so in thier discription )
; -
public		_XSet_Write_Page

PROC			_XSet_Write_Page   FAR

	ARG	Offst:WORD, XSize:WORD

	push		bp
	mov		bp,	sp		; Save Stack frame
	push		ds

	; Set the DS to local data
	ASSUME	ds:  @fardata
	mov	ax,  @fardata
	mov	ds, 	ax

	; First, load the page offset
	mov	ax,           [Offst]	; Get offset from pass
	mov	[Write_Page], ax	; Put it in var

	; Calculate the line offset
	mov     ax,	[XSize]		; Get the total size
	shr     ax, 1                   ; divided by 4 to make XBlocks
	shr     ax, 1			   
	mov     [WLine_Offset], ax	; Set line size in XBlocks

     ; !!DONE!!
	pop	ds             ; Return state
	pop	bp
	ret

ENDP			_XSet_Write_Page


; --------------------------- _XSet_Display_Page -------------------------------
; - This function set will set the display page.  The view will also be set
; -
public	_XSet_Display_Page

PROC	_XSet_Display_Page   FAR

	ARG   Offst:WORD, XSize:WORD, YSize:WORD, ViewX:WORD, ViewY:WORD


	push		bp
	mov		bp,	sp	; Set up stack frame
	push		ds 


     ; Set DS to local data
	ASSUME	ds:  @fardata
	mov	ax,  @fardata
	mov	ds,  ax		      

     ; Wait for retrace to end
	mov     dx, IN_STATUS1  ; Input Status #1 Register
   @Wait:
	in      al, dx           
	and     al, 08h         
	jnz     @Wait           
 
   ; Set line offset.  CRTC Offset register
     ; Calculate the line offset
	mov     ax,	        [XSize]  ; Get the total size
	mov     [DisplaySizeX], ax      ; Save it
	shr     ax, 1                   ; divide by 4 to make XBlocks
	shr     ax, 1
	mov     [DLine_Offset], ax	; Save XBlocks per line
	shr	   ax, 1			; divide by 2 to get register value
	
	; Set it
	mov	   dx, CRTC_INDEX  ; Set port
	mov     ah, 13h         ; CRTC Offset Register Index
	xchg    al, ah          ; Swap format
	out     dx, ax          ; Send it

   ; Set the Start Display Address to the new window
	mov     cx, [ViewX]     ; Get X start for the view
	mov     [ViewXLoc], cx  ; Save it for later
	mov     ax, [ViewY]     ; Get Y start for the view
	mov     [ViewYLoc], ax  ; Save it for later

	; Compute proper Display start address to use
	mul     [DLine_Offset]  ; AX = Y size (AX) * XBlocks per line
	shr     cx, 1
	shr     cx, 1    	    ; Convert CX to XBlocks.  Figure pan later
	add     cx, ax          ; Find view upper left pixel
	add     cx, [Offst]     ; Add page offset
	; NOTE: This will leave any 0-3 pan  for later

	; Change CRTC
	mov	   dx, CRTC_INDEX  ; Set port
	mov     al, 0Dh   	; Start display low.
	mov     ah, cl		; Load low 8 bits
	out     dx, ax          
	mov     al, 0Ch	; Start display high
	mov     ah, ch      ; Load high 8 Bits
	out     dx, ax
 
   ; Wait for a Vertical Retrace
	mov     dx, IN_STATUS1  
   @@Wait2:
	in      al, dx 
	and     al, 08h    	; Vertical Retrace Start?
	jz      @@Wait2         ; If Not, loop until it is
 
   ; Now Set the Horizontal Pixel Pan values
	mov	dx, 03C0h	; The Attribute controller
	mov	al, 033h	; Select Pixel Pan Register
	out  dx, al

     ; Get the pan value and send it 
	mov     ax, [ViewX]     ; Get raw X View start
	and     al, 03          ; Peel off the significant bits
	shl     al, 1           ; Shift for 256 Color Mode
	out     dx, al          ; Send it

   ; Set some data values for this display page
	
     ; Max View limits
	mov	ax, [XSize]	   ; Get the page size
	sub  ax, [ScreenSizeX] ; subtract the actual screen size
	dec	ax		        ; Adjust for count from 0
	mov	[MaxViewX], ax	   ; Save	

	mov	ax, [YSize]	    ; Get the page size
	mov  [DisplaySizeY], ax ; Save this
	sub  ax, [ScreenSizeY]  ; subtract the actual screen size
	dec	ax		         ; Adjust for count from 0
	mov	[MaxViewY], ax	    ; Save

     ; Save page offset
	mov	ax, [Offst]
	mov  [Display_Page], ax

   ; DONE!!
   @@Done:
	pop     ds             ; Return state
	pop     bp
	ret

ENDP			_XSet_Display_Page


; --------------------------- _XSet_View ----------------------------------
; - This function set will set the view port within the current page
; - Return 0 if successful, else 1 (TRUE for C)
public	_XSet_View

PROC	_XSet_View   FAR

	ARG   ViewX:WORD, ViewY:WORD


	push		bp
	mov		bp,	sp	; Set up stack frame
	push		ds 


	; Set DS to local data
	ASSUME	ds:  @fardata
	mov	ax,  @fardata
	mov	ds,  ax

     ; Wait for retrace to end
	mov     dx, IN_STATUS1  ; Input Status #1 Register
   @@Wait:
	in      al, dx           
	and     al, 08h         
	jnz     @@Wait           
 

   ; Set the Start Display Address to the new window
	mov     cx, [ViewX]      ; Get X start for the view
	cmp     cx, [MaxViewX]	; Is it within range
	ja      @@Error		; No, jump out.
	mov     [ViewXLoc], cx   ; Save the view location

	mov     ax, [ViewY]      ; Get Y start for the view
	cmp     ax, [MaxViewY]	; Is it within range
	ja      @@Error		; No, jump out.
	mov     [ViewYLoc], ax	; Save the view loc

     ; Compute proper Display start address to use
	mul     [DLine_Offset]  ; AX = Y size (AX) * XBlocks per line
	shr     cx, 1           
	shr     cx, 1    	    ; Conver CX to XBlocks.  Figure pan later
	add     cx, ax             ; Find view upper left pixel
	add     cx, [Display_Page] ; Add page offset
	; NOTE: This will leave any 0-3 pan  for later

     ; Change CRTC
	mov	   dx, CRTC_INDEX  ; Set port
	mov     al, 0Dh   	    ; Start display low.
	mov     ah, cl		    ; Load low 8 bits
	out     dx, ax
	mov     al, 0Ch	    ; Start display high
	mov     ah, ch          ; Load high 8 Bits
	out     dx, ax

   ; Wait for a Vertical Retrace
	mov     dx, IN_STATUS1
   @@Wait2:
	in      al, dx
	and     al, 08h    	; Vertical Retrace Start?
	jz      @@Wait2     ; If Not, loop until it is

   ; Now Set the Horizontal Pixel Pan values
	mov	dx, 03C0h	; The Attribute controller
	mov	al, 033h	; Select Pixel Pan Register
	out  dx, al

	; Get the pan value and send it
	mov     ax, [ViewX]     ; Get raw X View start
	and     al, 03          ; Peel off the significant bits
	shl     al, 1           ; Shift for 256 Color Mode
	out     dx, al          ; Send it

   ; DONE!!
	mov	ax,	0       ; No Error, Get outta here
	jmp     @@Done

   @@Error:			
	mov	ax,	1       ; Error

   @@Done:
	pop     ds             ; Return state
	pop     bp
	ret

ENDP	_XSet_View


; --------------------------- _XWait_Retrace -------------------------------
; - Wait for vertical retrace
public	_XWait_Retrace

PROC	_XWait_Retrace   FAR

   ; Wait for a Vertical Retrace
	mov     dx, IN_STATUS1
   @@Wait1:
	in      al, dx
	and     al, 08h    	; Vertical Retrace Start?
	jz      @@Wait1     ; If Not, loop until it is

	ret

ENDP	_XWait_Retrace


; --------------------------- _XSet_Box --------------------------------
; - This function will draw a box to the passed value.  The X coord must
; - be MOD 4 aligned.  
; -
public	_XSet_Box

PROC	_XSet_Box   FAR

	ARG   X_S:WORD,	Y_S:WORD, X_D:WORD, Y_D:WORD, COLOR:BYTE

	push		bp
	mov		bp,	sp			; Save Stack frame
	push		ds di si

	; Set DS to local data
	ASSUME	ds:  @fardata
	mov	ax,  @fardata	       
	mov	ds,  ax

	cld

     ; Set ES to display mem and DI to start of rectangle on screen
	mov	ax,  SCREEN_SEG
	mov	es,  ax

	mov 	ax,  [WLine_Offset]  ; Get the line length for write page
	mov     bx,  ax		     ; Save in BX for later		
	mul	[Y_S]		     ; Find Y offset value. Place in AX

	mov	di,  [X_S]     ; Find X offset value. Place in DI
	shr	di,  1
	shr	di,  1	       ; Adjust for planes

	add	di,  ax		    ; Add X and Y offsets
	add	di,  [Write_Page]   ; Add in page offset

     ; Set all data from CPU and non from latches
	mov	dx,  GC_INDEX
	mov	ax,  0FF00h + BIT_MASK  ; Set all CPU writes
	out	dx,  ax

	; Insure Map Mask is set to all planes
	mov	ax,  0F02h	; 0F sets all planes. 02 is Map Mask
	mov	dx,  SC_INDEX
	out	dx,  ax

     ; Insure X dist isn't 0 and put number of XBlocks in CX
	mov	cx,  [X_D]
	shr	cx,  1		; Div by 4 so we have
	shr	cx,  1          ; the actual number addresses to fill
				; per line
	cmp	cx,  0
	jle	@@Done		; Jump to stop if invalid


     ; SI will contain the number lines
	mov	si,	[Y_D]

     ; Load color into al
	mov	al,  [COLOR]

     ; Set BX to contain the distance from one scan line to next
	sub 	bx,	cx    ; BX was already loaded with line length

   @@Rowloop:
	push	cx	; Push cx so we can restore for each iteration

     ; Do the actual line.  REP STOSB.
	rep	stosb

     ; Adjust for next iteration
	pop	cx		; Restore CX. ( [Pix/line]/4 )
	add	di,	bx	; Point DI to start of next line

     ; End of Rowloop.  Dec SI, if it is NOT 0 then do next line
	dec	si
	jnz	@@Rowloop

     ; Else DONE!!

   @@Done:
	pop	si di ds
	pop     bp
	ret

ENDP			_XSet_Box


; --------------------------- _XClear -------------------------------
; - This will clear all memory on the video card to the passed value
; -
public	_XClear

PROC	_XClear   FAR

	ARG	COLOR:WORD

	push	bp
	mov	bp,	sp			; Save Stack frame
	push	di

     ; Set MAP MASK to all planes
	mov	dx,	SC_INDEX		
	mov	ax,	0F02h
	out	dx,	ax

     ; Clear the screen
	mov	ax,  SCREEN_SEG
	mov	es,  ax
	sub	di,  di
	sub	ax,  ax
	mov	ax,  [COLOR]
	mov     ah,  al
	mov	cx,  8000h
	rep	stosw

     ; DONE!
	pop	di   ; Return state
	pop	bp
	ret

ENDP			_XClear


; --------------------------- _XPut_Tile -------------------------------
; - This function will place a tile on the current write screen
; - It will be done a plane at a time and will expect the image data
; - to be laid out one plane at a time.
; - If there is an even number of XBlocks then a word write will be used
; -
public		_XPut_Tile

PROC			_XPut_Tile   FAR

	ARG	X_S:WORD,	Y_S:WORD, X_D:WORD,	Y_D:WORD,	IMAGE_OFF:DWORD

			push		bp
			mov		bp,	sp			; Save Stack frame
			push		ds di si

			cld

		; Set all data from CPU and non from latches
			mov		dx,	GC_INDEX
			mov		ax,	0FF00h + BIT_MASK  ; Set all CPU writes
			out		dx, 	ax

		; Get the Screen Seg and put it in es and load ds to fardata
			mov		ax,  SCREEN_SEG
			mov		es,  ax

			ASSUME	ds:  @fardata
			mov		ax,  @fardata
			mov		ds,  ax			; Set DS to local fardata

		; Load CX with the number of XBlocks per image line
			mov		cx,	  [X_D]
			shr		cx,	  1
			shr		cx,	  1         ; Divide by 4 to get
			mov		[X_D], cx		  ; Save CX as new Delta

		; Load di with destination offset and save it in DEST
			mov		ax,       [WLine_Offset]	; Size of line in XBlocks
			mov		bx,	ax				; Save in DX
			mul		[Y_S]

			mov		di,	[X_S]
			shr		di,  1
			shr		di,	1

			add       di,	[Write_Page]
			add		di,	ax            ; Di is now loaded

			mov       [X_S],	di       ; Store it.  Steal X start

		; Set [Y_S] to the distance from one scan line to next
			sub		bx,		cx		     ; minus number of XBlocks
			mov	     [Y_S],    bx			; Move to store

		; Set BX to the number of scan lines
			mov		bx,  	[Y_D]

		; Load ds:si with source
			lds		si,	[IMAGE_OFF]

		; Set up DX and AX for plane selection
			mov		al,  02h		; Select Map Mask
			mov		dx,	SC_INDEX
			out		dx,	al
			inc		dx
			mov		al,	011h		; Set for plane selection

		; Save CX in AH
			mov		ah,	cl

		; This section is for the WRITES ------------------------------

	@@PlanesW:          ; Master plane loop.

			out		dx,  al        ; Set map mask
			mov		dx,	[Y_S]     ; Steal DX to speed up add

	@@RowloopW:         	; Scan line loop

		; Do the actual line.  REP MOVSW.
			shr		cx,	1		; Shift for word
			rep		movsw
			adc		cx,	0		; Add 1(carry) to CX if dangling byte
			rep 		movsb		; Write dangle if there

		; Adjust for next iteration
			mov		cl,  ah		; Restore CX
			add		di,	dx		; Point DI to start of next line

		; !!End of Rowloop.  Dec BX, if it is NOT 0 then do next line
			dec		bx
			jnz		@@RowloopW

		; Done with plane.  Reset Destination pointers.  Adjst plane
			mov		di,	[X_S]     ; Restore destination
			mov		bx,	[Y_D]	; Restore scan line count

		; !!End of Plane loop.
			mov		dx, (SC_INDEX+1) ; Restore DX for next plane
			shl       al, 1            ; Shift up a plane.  Carry set = done
			jnc		@@PlanesW		  ; If carry set, drop out

		; !!!DONE
			pop		si di ds             ; Return state
			pop		bp
			ret

ENDP			_XPut_Tile


; --------------------------- _XTile_Size -----------------------------------
; - This function will return the total number of bytes an image requires
; - Div by 4 will yield the number of XBlocks
public		_XTile_Size

PROC			_XTile_Size   FAR

	ARG	 X_D:WORD,	Y_D:WORD


			push		bp
			mov		bp,	sp		; Save Stack frame

		; Load AX with the total number of bytes
			mov		cx,	[X_D]
			mov		ax,	[Y_D]
			mul		cx	  	  ; Yield is total bytes

		; AX holds return
			pop		bp
               ret

ENDP			_XTile_Size


; --------------------------- _XUpload_Tile -------------------------------
; - This function will place a tile in the display memory
; - It will be done a plane at a time and will expect the image data
; - to be laid out one plane at a time.
; - The tile will be laid out in a linear manner and even though it may be
; - sent to the current screen it will not be correctly displayed
; -
public		_XUpload_Tile
public		_XUpload_Sprite
LABEL          _XUpload_Sprite	FAR

PROC			_XUpload_Tile   FAR

	ARG 	  DEST:WORD, ISIZE:WORD, IMAGE_OFF:DWORD

			push		bp
			mov		bp,	sp			; Save Stack frame
			push		ds di si

			cld

		; Get the Screen Seg and put it in es
			mov		ax,  SCREEN_SEG
			mov		es,  ax


		; Load di with destination offset and save it in DEST
			mov		di,		[DEST]
			mov       bx,		di       ; Store it

		; Load ds:si with source
			lds		si,	[IMAGE_OFF]

		; Get number of XBlocks per plane.
			mov		cx,  [ISIZE]
			shr		cx,  1
			shr		cx,  1
			mov		bp,  cx	; Save size in BP

		; Set all data from CPU and non from latches
			mov		dx,	GC_INDEX
			mov		ax,	0FF00h + BIT_MASK  ; Set all CPU writes
			out		dx, 	ax

		; Set up DX and AX for plane selection
			mov		al,	02h		; Select the MAP MASK
			mov		dx,	SC_INDEX
			out		dx,  al
			inc		dx
			mov		al,	11h		; Plane 0

		; This section is for WRITES ------------------------------
	@@BeginWrite:

	@@PlanesW:       		; Master plane loop.

			out	     dx,	al	; Set the plane

		; Do the actual line.  REP MOVSW.
			shr		cx,	1	; Set for word writes
			rep		movsw
			adc		cx,	0	; Add 1(carry) to CX to see if dangle
			rep       movsb	; Write the dangle if there.

		; Done with plane.  Reset Destination pointers.  Adjst plane
			mov		di,	bx		; Restore Destination
			mov		cx,  bp		; Restore CX.

		; !!End of Plane loop.
			shl		al,	1		; Shift plane selector
			jnc		@@PlanesW		; If no carry, then more to do


		; !!DONE!!
			pop		si di ds            ; Return state
			pop		bp
			ret

ENDP			_XUpload_Tile


; --------------------------- _XPaste_Tile -------------------------------
; - This function will place a tile from the display memory to current screen
; -
public		_XPaste_Tile

PROC			_XPaste_Tile   FAR


	ARG	X_S:WORD,	Y_S:WORD, X_D:WORD,	Y_D:WORD,	TILE:WORD

			push		bp
			mov		bp,	sp		; Save Stack frame
			push		ds di si

               cld

		; Load DS.

			ASSUME	ds:  @fardata
			mov		ax,  @fardata
			mov		ds,  ax			; Set DS to segment XTile data

		; Set DI to start of rectangle on screen
			mov 		ax,  [WLine_Offset]
			mul		[Y_S]		   ; Find Y offset value. Place in AX

			mov		di,  [X_S]        ; Find X offset value. Place in DI
			shr		di,  1
			shr		di,  1

			add		di,  ax		   ; Add X and Y offsets
			add		di,  [Write_Page] ; Add in page offset

		; Set all data from latches
			mov		dx,	GC_INDEX
			mov		ax,	00000h + BIT_MASK  ; Set all latch writes
			out		dx, 	ax

		; Insure Map Mask is set to all planes
			mov		ax,  0F02h	; 0F sets all planes. 02 is Map Mask
			mov		dx,	SC_INDEX
			out		dx,	ax

		; Load CX with XBlocks per scan line
			mov		cx,  [X_D]
			shr		cx,  1		; Div by 4 so we have
			shr		cx,  1         ; Xblock count

		; Set AX to the distance from one scan line to next
			mov		ax,		[WLine_Offset]
			sub		ax,		cx

		; Set SI to source
			mov		si,		[TILE]

		; Get the Screen Seg and put it in DS and ES
			mov		dx,  SCREEN_SEG
			mov		ds,  dx
			mov		es,	dx

		; Set DX to the number of scan lines
			mov		dx,  	[Y_D]

		; Save the CX in BP
			mov		bp,		cx

		; This section is for WRITES ------------------------------
	@@Rowloop:

		; Do the actual line.  REP MOVSB.
			rep		movsb

		; Adjust for next iteration
			mov		cx,  bp	; Restore CX.
			add		di,	ax	; Point DI to start of next line

		; End of Rowloop.  Dec DX, if it is NOT 0 then do next line
			dec		dx
			jnz		@@Rowloop

		; !!DONE!!
			pop		si di ds             ; Return state
			pop		bp
			ret

ENDP			_XPaste_Tile; 


;-------------------------- _XPaste_Sprite ------------------------------
; - This function will place a sprite from the display memory to current screen
; - It will NOT do masking
public		_XPaste_Sprite

PROC			_XPaste_Sprite   FAR


	ARG	X_S:WORD,	Y_S:WORD, X_D:WORD,	Y_D:WORD,	TILE:WORD

			push		bp
			mov		bp,	sp		; Save Stack frame
			push		ds di si

               cld

		; Load DS.

			ASSUME	ds:  @fardata
			mov		ax,  @fardata
			mov		ds,  ax			; Set DS to segment XTile data

		; Set DI to start of rectangle on screen
			mov 		ax,  [WLine_Offset]
			mul		[Y_S]		   ; Find Y offset value. Place in AX

			mov		di,  [X_S]        ; Find X offset value. Place in DI
			shr		di,  1
			shr		di,  1		

			add		di,  ax		   ; Add X and Y offsets
			add		di,  [Write_Page] ; Add in page offset

		; Set SI to source
			mov		si,		[TILE]

		; Find adjustment to source for alignment
			mov		ax,	[X_D]
			mov		cx,	[X_S]

			shr		ax,	1	   ; Number of XBlocks a line	
			shr		ax,	1
			mul		[Y_D]        ; Find size of each sprite alignment
			
			and		cx,	3	   ; Mask all but 2 LSBs
			
			mul		cx		   ; Find new offset				

			add		si,	ax	   ; Adjust SI	

		; Get the Screen Seg and put it in DS and ES
			mov		ax,  SCREEN_SEG
			mov		ds,  ax
			mov		es,	ax

		; Set all data from latches
			mov		dx,	GC_INDEX
			mov		ax,	00000h + BIT_MASK  ; Set all latch writes
			out		dx, 	ax

		; Insure Map Mask is set to all planes
			mov		ax,  0F02h	; 0F sets all planes. 02 is Map Mask
			mov		dx,	SC_INDEX
			out		dx,	ax

		; Load CX with XBlocks per scan line
			mov		cx,  [X_D]
			shr		cx,  1		; Div by 4 so we have
			shr		cx,  1         ; Xblock count

		; Set AX to the distance from one scan line to next
			mov		ax,		[WLine_Offset]
			sub		ax,		cx

		; Set DX to the number of scan lines
			mov		dx,  	[Y_D]

		; Save CX in BP
			mov		bp, 		cx

		; This section is for WRITES ------------------------------
	@@Rowloop:

		; Do the actual line.  REP MOVSB.
			rep		movsb

		; Adjust for next iteration
			mov		cx,  bp	; Restore CX.
			add		di,	ax	; Point DI to start of next line

		; End of Rowloop.  Dec DX, if it is NOT 0 then do next line
			dec		dx
			jnz		@@Rowloop

		; !!DONE!!
			pop		si di ds             ; Return state
			pop		bp
			ret

ENDP			_XPaste_Sprite


; --------------------------- _XPut_Tile_M -------------------------------
; - This function will place a tile on the current write screen
; - It will be done a plane at a time and will expect the image data
; - to be laid out one plane at a time.
; - All 0 pixels will leave the screen intact.
; -
public		_XPut_Tile_M

PROC			_XPut_Tile_M   FAR

	ARG	X_S:WORD,	Y_S:WORD, X_D:WORD,	Y_D:WORD,	IMAGE_OFF:DWORD

			push		bp
			mov		bp,	sp			; Save Stack frame
			push		ds di si

			cld

		; Get the Screen Seg and put it in es
			mov		ax,  SCREEN_SEG
			mov		es,  ax


		; Load CX with the number of XBlocks per scan line
			mov		cx,	[X_D]
			shr		cx,	1
			shr		cx,	1         ; Divide by 4 to get

		; Set [X_D] to the distance from one scan line to next
			mov		ax,		[WLine_Offset]
			sub		ax,		cx
			mov		[X_D],	ax

		; Save number of XBlocks a line in upper cx
			mov		ch,		cl

		; Set BL to the number of scan lines and save
			mov		bx,  	[Y_D]

		; Load DS.
			ASSUME	ds:  @fardata
			mov		ax,  @fardata
			mov		ds,  ax			; Set DS to fardata segment

		; Load di with destination offset and save it in DEST
			mov		ax,		[WLine_Offset]
			mul		[Y_S]

			mov		di,	[X_S]
			shr		di,  1
			shr		di,	1             ; Adjust to number of XBlocks

			add       di,	[Write_Page]
			add		di,	ax            ; Di is now loaded

			mov       [X_S],	di       ; Store it

		; Load ds:si with source
			lds		si,	[IMAGE_OFF]

		; Set all data from CPU and non from latches
			mov		dx,	GC_INDEX
			mov		ax,	0FF00h + BIT_MASK  ; Set all CPU writes
			out		dx, 	ax

		; Set up DX and AX for plane selection
			mov		al,	02h	; Plane 0 selected
			mov		dx,	SC_INDEX
			inc		dx
			mov		ax,	0101h	; Set up AX

		; This section is for the WRITE ------------------------------
	@@PlanesB:          ; Master plane loop.

			out		dx,	al   ; Set the plane
	@@RowloopB:         	; Scan line line

		; Do the actual line.
	@@PixelLoop:
			lodsb				; Get the pixel
			or		al,	al		; Is it zero
			jz		@@NoDraw       ; If zero, don't draw it
			mov		[es:di],	al	; Else, do draw it

	@@NoDraw:
			inc		si             ; Move to next pixel
			inc		di
			dec       cl
			jnz		@@PixelLoop

		; Adjust for next iteration
			mov		cl,  ch		; Restore CL. ( [Pix/line]/4 )
			add		di,	[X_D]	; Point DI to start of next line

		; !!End of Rowloop.  Dec SI, if it is NOT 0 then do next line
			dec		bx
			jnz		@@RowloopB

		; Done with plane.  Reset Destination pointers.  Adjst plane
			mov		di,	[X_S]     ; Restore destination
			mov		bx,	[Y_D]	; Restore scan line count
			shl		ah,	1		; Adjust plane
			mov		ah,  al		; Save the
			jnc		@@PlanesB      ; If no carry, then more planes

		; !!!DONE
			pop		si di ds             ; Return state
			ret

ENDP			_XPut_Tile_M


; --------------------------- _XMove_Tile ----------------------------------
; - This function will move a tile about on the current write page
; - Coord, Size, and destination must be givin
public		_XMove_Tile

PROC			_XMove_Tile   FAR

  ARG  X_S:WORD, Y_S:WORD, X_E:WORD, Y_E:WORD, X_D:WORD, Y_D:WORD

			push		bp
			mov		bp,	sp		; Save Stack frame
			push		ds si di

			cld

		; Load DS.
			ASSUME	ds:  @fardata
			mov		ax,  @fardata
			mov		ds,  ax			; Set DS to fardata segment

		; Load si with source X in XBlocks
			mov		si,	[X_S]
			shr		si,  1
			shr		si,  1

		; Load di with destination X in XBlocks
			mov		di,	[X_D]
			shr		di,  1
			shr		di,  1

		; Convert [X_E] to width in XBlocks
			mov		bx,  [X_E]
			shr		bx,  1
			shr		bx,  1

			mov		ax,     bx	; Save in AX
			mov		[X_E],  bx	; Put back

		; Determine the X direction
			cmp		si,     di
			jge		@@GoLeft

		; Going right.  Copy right to left
			add		si,	   ax	; Source starts at right edge
			add		di,	   ax	; Same with destination
			neg		bx			; Make negative
			std					; Move backwards on string instructions

		; Determine the Y direction
	@@GoLeft:
			mov		cx,  [WLine_Offset]
			mov		ax,  [Y_S]     ; Get the Y source start
			mov		dx,  [Y_D]	; Get the Y destination

			cmp		ax,	dx		; Going up or down
			jge       @@GoUp

		; Rectangle going down.  Copy bottom to top
			mov		ax,  [Y_E]	; Get the bottom coord
			add		dx,	ax		; Add it to the destination, so
								; it points to end of destination blk
			neg		cx			; Make CX negative
			add		ax,	[Y_S]	; Point AX to end of source blk
								; By adding size to start

		; Calcuate the offsets
	@@GoUp:
			push		dx			; Save DX the torment of a multiply
			mul		[WLine_Offset]	; Find Y adder for source
			add		si,	ax		; Add it

			pop		ax			; Pop off the destination Y into AX
			mul		[WLine_Offset]	; Find Y adder for destination
			add		di,	ax		; Add it

			sub		cx, 	bx		; Find the scan line data

		; Add in the write page offset
			add		si,	[Write_Page]
			add		di,	[Write_Page]

		; Get the Screen Seg and put it in DS and ES
			mov		ax,  SCREEN_SEG
			mov		ds,  ax
			mov		es,	ax

		; Set all data from latches
			mov		dx,	GC_INDEX
			mov		ax,	00000h + BIT_MASK  ; Set all latch writes
			out		dx, 	ax

		; Insure Map Mask is set to all planes
			mov		ax,  0F02h	; 0F sets all planes. 02 is Map Mask
			mov		dx,	SC_INDEX
			out		dx,	ax

		; Set AX to the Scan line lenghth
			mov		ax,	[X_E]

		; Set BX to the Y size
			mov		bx,	[Y_E]

		; Set DX to the Scan line differential
			mov		dx,  cx

	  ; Do the WRITES---------------------
	@@ScanLoop:

			mov	    	cx,  ax	; Set cx to the Scan size
			rep 		movsb	; Do the line

		; Add the scan line differential to source and dest
			add		si,	dx
			add		di,	dx

		; See if done with all lines
			dec		bx
			jnz		@@ScanLoop	; No, then go and do another line

		; !!DONE!!
			pop		di si ds
			pop		bp			; Return state
			ret

ENDP			_XMove_Tile


; --------------------------- _XRegister_Font8 ------------------------------
; - This function will register a 8-font.
; - The function will return in AX the actual number of bytes used.
; - The function expects each character to be laid out as for Upload_Tile,
; - one character at a time.  Each character is 8x by 10y.

public		_XRegister_Font8

PROC			_XRegister_Font8   FAR

	ARG 	  DEST:WORD, NUMBER:WORD, BASE:WORD, UPLOAD:WORD, FONT_OFF:DWORD

			push		bp
			mov		bp,	sp			; Save Stack frame
			push		ds di si

			cld

		; Load DS.
			ASSUME	ds:  @fardata
			mov		ax,  @fardata
			mov		ds,  ax			; Set DS to fardata segment

		; Save the base char
			mov		ax,			[BASE]
			mov		[Char_Base8],	ax

		; Will we upload this font?
			mov		cx,	[UPLOAD]
			mov		[UpLoaded8], cx	; Flag it	( 0 is uploaded )
			jcxz		@@UploadFont		; Yes!, then jump to it

		; Don't upload this font.  Just register and return.
			les		si,			  [FONT_OFF]
			mov		[Font_SiteD8],   si
			mov		ax,			  es
			mov		[Font_SiteD8+2], ax
			jmp       @@DONE

	@@UploadFont:
		; Check font size to insure no overrun
			mov		dx,	20
			mov		ax,	[NUMBER]
			mul		dx		  	; Mul number of chars by 20bytes
			add		ax,  [DEST]	; Add destination to get proposed end
			cmp		ax,	STORE	; Where is it in relation to begin
			ja		@@CONT		; If above, no wrap of AX, OK
			mov		ax,	0		; Else leave 0 in AX, indicating ERR
			jmp		@@DONE		; And jump out

	@@CONT:
		; Save the size and location
			mov		ax,            [DEST]
			mov		[Font_SiteU8],	ax

		; Load di with destination offset and save it in DX
			mov		di,		ax	    ; AX retains it from prev action
			mov       dx,       ax       ; Store it

		; Load ds:si with source
			lds		si,  [FONT_OFF]

		; Load es with SCREEN SEG
			mov		ax,  SCREEN_SEG
			mov		es,	ax

		; This section iterates for each char----------------------------
			mov		bx,	[NUMBER]	; Set BL to number of characters

		; Set BP to destination, as was in DX
			mov		bp,  dx

		; Set all data from CPU and non from latches
			mov		dx,	GC_INDEX
			mov		ax,	0FF00h + BIT_MASK  ; Set all CPU writes
			out		dx, 	ax

		; Set up DX plane selection
			mov		dx,	SC_INDEX

		; Set CX to number of scan lines in a char
			mov		cx,	10        ; Set CX to 10 scan lines

	@@BeginWrite:

		; Set AX up for plane selection
			mov		ax,	1102h	; Plane 0 selected

		; This section loops through the planes for each char -----------
	@@PlanesW:       			; Master plane loop.

			out	     dx,	ax	; Set the plane

		; Do the actual line.  REP MOVSW.  Move both XBlocks on each line
			rep		movsw

	  ; Done with plane.  Reset Destination pointers.  Adjst plane
			mov		di,	bp
			shl		ah,	1		; Adjust plane. Carry set if done
			mov		cx,  10	     ; Restore CX.

		; !!End of Plane loop.
			jnc		@@PlanesW      ; If no carry, not done with char

		; !!End of Char loop.

			add		bp,	20		; Adjust destination for next char
			mov		di,	bp		; Store it in DI

			dec		bx             ; Another char done
			jnz		@@BeginWrite   ; If not zero, not done with font set

		; !!DONE!!
		@@DONE:
			pop		si di ds            ; Return state
			pop		bp
			ret

ENDP			_XRegister_Font8


; ----------------------------- _XChar8 ----------------------------------
; - This function will place a char from the current font to current screen
; - Uses the 8pix font
public		_XChar8

PROC			_XChar8   FAR

	ARG	X_S:WORD,	Y_S:WORD, CHAR:WORD

			push		bp
			mov		bp,	sp		; Save Stack frame
			push		ds di si

			cld

		; Load DS.
			ASSUME	ds:  @fardata
			mov		ax,  @fardata
			mov		ds,  ax			; Set DS to segment w/ Table

		; Set DI to start of character on screen
			mov 		ax,  [WLine_Offset]
			mul		[Y_S]		   ; Find Y offset value. Place in AX

			mov		di,  [X_S]        ; Find X offset value. Place in DI
			shr		di,  1
			shr		di,  1		   ; Adjust to number of XBlocks

			add		di,  ax		   ; Add X and Y offsets
			add		di,  [Write_Page] ; Add in page offset

		; Is the font uploaded or not
			mov		ax,	[UpLoaded8]
			cmp		ax,	UPLOADFONT
			je		@@FontIsUploaded	; The font is uploaded

	  ; - Font is not uploaded
		; Set CX to the scan line differential
			mov		cx,  [WLine_Offset]
			sub		cx,  2			; Subtract the XBlocks of a char

		; Figure character offset and add to load ds:si font site
			mov		ax,	80			; Put font size in ax
			xor		bx,	bx			; Clear bx
			mov		bx,	[CHAR]         ; Put character place in BX
			sub		bx,	[Char_Base8]	; Subtract base char
			mul		bx				; Mul size by place
			lds		si,  [Font_Addr8]
			add		si,  ax

		; Move the scan line differential from CX to BP
			mov		bp,  cx

		; Save the base destination in CX
			mov		cx,  di

		; Get the Screen Seg and put it in ES
			mov		ax,  SCREEN_SEG
			mov		es,	ax

		; Set all data from CPU and non from latches
			mov		dx,	GC_INDEX
			mov		ax,	0FF00h + BIT_MASK  ; Set all CPU writes
			out		dx, 	ax

		; Set up DX and AX for plane selection
			mov		ax,	1102h	; Plane 0 selected
			mov		dx,	SC_INDEX

		; This section is for WRITES ------------------------------

		; Set BH to number of lines to do
			mov       bx,  10		; 10 scan lines per 8-pix font

	@@PlanesW:          ; Master plane loop.

			out		dx,	ax	; Set the plane

	@@RowloopW:         	; Scan line loop

		; Do the actual line.  MOVSW.
			movsw

		; Adjust for next iteration
			add		di,	bp		; Point DI to start of next line

		; !!End of Rowloop.  Dec BH, if it is NOT 0 then do next line
			dec		bx
			jnz		@@RowloopW

		; Done with plane.  Reset Destination pointers.  Adjst plane
			mov		di,	cx		; Restore the destination start
			mov		bx,	10		; Restore scan line count
			shl		ah,	1		; Adjust plane

		; !!End of Plane loop.
			jnc		@@PlanesW		; If no carry, then more to do.



		; Done with this
			jmp		@@DONE


	  ; - Font is uploaded
	  @@FontIsUploaded:
		; Calc the source and put in SI
			mov		si,	[Font_SiteU8]	; Get base location
			mov		ax,	20			; Put font size in ax (in XBlock)
			mov		bx,	[CHAR]         ; Put character place in BX
			sub		bx,	[Char_Base8]	; Subtract base char
			mul		bx				; Mul size by place
			add		si,	ax			; Put in SI

		; Insure Map Mask is set to all planes
			mov		ax,  0F02h	; 0F sets all planes. 02 is Map Mask
			mov		dx,	SC_INDEX
			out		dx,	ax

		; Set all data from latches
			mov		dx,	GC_INDEX
			mov		ax,	00000h + BIT_MASK  ; Set all latch writes
			out		dx, 	ax

		; Set AX to line dif.
			mov		ax,	[WLine_Offset]		; Page width
			sub	     ax,  2                   ; minus XBlocks per char

		; Get the Screen Seg and put it in DS and ES
			mov		dx,  SCREEN_SEG
			mov		ds,  dx
			mov		es,	dx

		; Set CX to the number of scan lines
			mov		cx,		10

		; This section is for WRITES ------------------------------
	@@Rowloop:

		; Do the actual line.  MOVSB.
					movsb
					movsb

		; Adjust for next iteration
			add		di,	ax	; Point DI to start of next line

		; End of Rowloop.
			loop		@@Rowloop

		; !!DONE!!
	@@DONE:
			pop		si di ds             ; Return state
			pop		bp
			ret

ENDP			_XChar8;


; ------------------------ _Register_Font_Masks8 ---------------------------
; - This function registers a font mask for the current 8pix font
; - This mask can be used for MFont character drawing
public		_Register_Font_Masks8

PROC			_Register_Font_Masks8   FAR

  	ARG	MASK_OFF:WORD, MASK_SEG:WORD  

			push		bp
			mov		bp,	sp		; Save Stack frame
			push		ds

		; Load DS.
 			ASSUME	ds:  @fardata
			mov		ax,  @fardata
			mov		ds,  ax			; Set DS to fardata segment 

		; Move location to local
			mov		ax,			[MASK_OFF]
			mov       [Font_Mask_O8], ax

			mov		ax,			[MASK_SEG]
			mov       [Font_Mask_S8], ax

		; !!DONE!!
			pop		ds
			pop		bp			; Return state
			ret

ENDP			_Register_Font_Masks8


; ----------------------------- _XChar8_M ----------------------------------
; - This function will place a char from the current font to current screen
; - This function requires a font mask set to be registered.
; - Uses the 8pix font
public		_XChar8_M

PROC			_XChar8_M   FAR

	ARG	X_S:WORD,	Y_S:WORD, CHAR:WORD


			push		bp
			mov		bp,	sp			; Save Stack frame
			push		ds di si

			cld

		; Load DS.
			ASSUME	ds:  @fardata
			mov		ax,  @fardata
			mov		ds,  ax			; Set DS to fardata segment

		; Set DI to start of rectangle on screen
			mov 		ax,  [WLine_Offset]
			mov		bx,	ax		   ; Save in BX for later
			mul		[Y_S]		   ; Find Y offset value. Place in AX

			mov		di,  [X_S]        ; Find X offset value. Place in DI
			shr		di,  1
			shr		di,  1

			add		di,  ax		   ; Add X and Y offsets
			add		di,  [Write_Page] ; Add in page offset

		; Is the font uploaded or not
			mov		cx,	[UpLoaded8]  ; UPLOADED is 0!
			jcxz		@@Uploaded	   ; The font is uploaded

	  ; - Font is not uploaded

		; Figure character offset and add to load ds:si font site
			mov		ax,	80			; Put font size in ax
			mov		cx,	[CHAR]         ; Put character place in BX
			sub		cx,	[Char_Base8]	; Subtract base char
			mul		cx				; Mul size by place
			lds		si,  [Font_Addr8]
			add		si,  ax

		; Set BX to the line differential
			dec		bx			; BX was set above - first XBlock

		; Save the base destination in BP
			mov		bp,  di

		; Get the Screen Seg and put it in ES
			mov		ax,  SCREEN_SEG
			mov		es,	ax

		; Set all data from CPU and non from latches
			mov		dx,	GC_INDEX
			mov		ax,	0FF00h + BIT_MASK  ; Set all CPU writes
			out		dx, 	ax

		; Set up DX and AX for plane selection
			mov		ax,	0102h	; Plane 0 selected
			mov		dx,	SC_INDEX

		; This section is for WORD WRITES ------------------------------

		; Set CX to number of lines to do
			mov       cx,  10		; 10 scan lines per 8-pix font

	@@PlanesW:          ; Master plane loop.

			out		dx,	ax	; Set the plane

	@@RowloopW:         	; Scan line loop

		; Do the actual DRAW.  MOV.
			lodsb	; Get the pixel
			or		al,	al		; Is it zero
			jz		@@NoDraw1       ; If zero, don't draw it
			mov		[es:di],	al	; Else, do draw it

	@@NoDraw1:
			inc		di

			lodsb				; Get the pixel
			or		al,	al		; Is it zero
			jz		@@NoDraw2      ; If zero, don't draw it
			mov		[es:di],	al	; Else, do draw it

	@@NoDraw2:
		; Adjust for next iteration
			add		di,	bx		; Point DI to start of next line

		; !!End of Rowloop.  Dec CX, if it is NOT 0 then do next line
			loop		@@RowloopW

		; Done with plane.  Reset Destination pointers.  Adjst plane
			mov		di,	bp		; Restore the destination start
			mov		cx,	10		; Restore scan line count

			mov		al,  02h		; Restore MAP MASK pointer
			shl		ah,	1		; Adjust plane

		; !!End of Plane loop. If carry not set, then not done
			jnc		@@PlanesW

		; Done with this
			jmp		@@DONE


	@@Uploaded:
	  ; - Font is uploaded
		; Set SI to mask offset
			mov		ax,	10			; Put mask size in ax
			mov		cx,	[CHAR]         ; Put character place in CX
			sub		cx,	[Char_Base8]	; Subtract base char
			mul		cx				; Mul size by place
			add		ax,	[Font_Mask_O8] ; Find masks offset in mask set
			mov		si,	ax			; Save it

		; Calc the source and put in BX
			mov		bx,	[Font_SiteU8]	; Get base location
			mov		ax,	20			; Put font size in ax
			mov		cx,	[CHAR]         ; Put character place in BX
			sub		cx,	[Char_Base8]	; Subtract base char
			mul		cx				; Mul size by place
			add		bx,	ax			; Put in SI

		; Set BP to the scan line differential
			mov		bp,	[WLine_Offset]
			dec		bp

		; Set DS to mask area
			mov		ax,	[Font_Mask_S8]
			mov 		ds,	ax

		; Get the Screen Seg and put it in ES
			mov		ax,  SCREEN_SEG
			mov		es,	ax

		; Set all data from latches
			mov		dx,	GC_INDEX
			mov		ax,	00000h + BIT_MASK  ; Set all latch writes
			out		dx, 	ax

		; Set up Map Mask
			mov		al,  02h	; 02 is Map Mask
			mov		dx,	SC_INDEX
			out		dx,	al
			inc		dx

		; Set CH to the number of scan lines
			mov		ch,	10

		; Set CL to shift spaces
			mov		cl,	4

		; This section is for WRITES ------------------------------
	@@Rowloop:
		; This section prep the Map Mask
			lodsb				; Get mask byte

		; Set mask for first write
			out		dx,	al		; Set map mask

		; Write the first byte
			mov		ah,		[es:bx]
			mov		[es:di],	ah

		; Adjust di, bx
			inc		di
			inc		bx

		; Set mask for second write
			shr		al,       cl		; Move upper nibble down
			out		dx,		al        ; Set map mask

		; Write the second byte
			mov		ah,		[es:bx]
			mov		[es:di],	ah

		; Adjust bx
			inc		bx

		; Adjust for next iteration
			add		di,	bp	; Point DI to start of next line

		; End of Rowloop.
			dec		ch
			jnz		@@Rowloop

		; !!DONE!!
	@@DONE:
			pop		si di ds             ; Return state
			pop		bp
			ret

ENDP			_XChar8_M;


;----------------------------- _XString8 ----------------------------------
; - This function will place a char from the current font to current screen
; - It will use the masked font on mask flag set to not 0.
public		_XString8

PROC			_XString8   FAR

	ARG	X_S:WORD,	Y_S:WORD, MASKIT:WORD, STR_OFF:DWORD

			push		bp
			mov		bp,	sp		; Save Stack frame
			push		ds di si

		; Load DS.
			ASSUME	ds:  @fardata
			mov		ax,  @fardata
			mov		ds,  ax			; Set DS to segment w/ Table

		; Move flag into CX
			mov		cx,  [MASKIT]

		; Load DS:SI
			lds		si,  [STR_OFF]

		; !!! This is the master string loop
	@@STRING:
			xor       ax,	ax
			mov	  	al,	[ds:si]		; Get the char
			cmp		al,	0			; Is it the EOS
			je		@@DONE			; If so jump

		 ; Save cx
			push 	cx

		 ; Build stack frame
			push		ax				; Push the char
			push		[Y_S]			; Push the Y coor
			push		[X_S]			; Push the X coor
		 ; To mask or not to mask
			jcxz		@@DontMask		; If Flag = 0, dont mask
			call		_XChar8_M			; Put the masked char
			jmp		@@Continue

	@@DontMask:
			call      _XChar8			; Don't mask

	@@Continue:
			add		sp,	6			; Adjust the stack

			pop		cx				; Restore cx

			add		[X_S],	8		; Move the cursor
			inc		si				; Point to next char
			jmp		@@STRING            ; Continue

		; !!DONE!!
	@@DONE:
			pop		si di ds             ; Return state
			pop		bp
			ret

ENDP			_XString8


; --------------------------- _XRegister_Font4 ------------------------------
; - This function will register a 4-font.
; - The function will return in AX the actual number of bytes used.
; - The function expects each character to be laid out as for Upload_Tile,
; - one character at a time.  Each character is 4x by 6y.

public		_XRegister_Font4

PROC			_XRegister_Font4   FAR

	ARG 	  DEST:WORD, NUMBER:WORD, BASE:WORD, UPLOAD:WORD, FONT_OFF:DWORD

			push		bp
			mov		bp,	sp			; Save Stack frame
			push		ds di si

			cld

		; Load DS.
			ASSUME	ds:  @fardata
			mov		ax,  @fardata
			mov		ds,  ax			; Set DS to fardata segment

		; Save the base char
			mov		ax,			[BASE]
			mov		[Char_Base4],	ax

		; Will we upload this font?
			mov		cx,	[UPLOAD]
			mov		[UpLoaded4], cx	; Flag it	( 0 is uploaded )
			jcxz		@@UploadFont		; Yes!, then jump to it

		; Don't upload this font.  Just register and return.
			les		si,			  [FONT_OFF]
			mov		[Font_SiteD4],   si
			mov		ax,			  es
			mov		[Font_SiteD4+2], ax
			jmp       @@DONE

	@@UploadFont:
		; Check font size to insure no overrun
			mov		dx,	6
			mov		ax,	[NUMBER]
			mul		dx		  	; Mul number of chars by 20bytes
			add		ax,  [DEST]	; Add destination to get proposed end
			cmp		ax,	STORE	; Where is it in relation to begin
			ja		@@CONT		; If above, no wrap of AX, OK
			mov		ax,	0		; Else leave 0 in AX, indicating ERR
			jmp		@@DONE		; And jump out

	@@CONT:
		; Save the size and location
			mov		ax,            [DEST]
			mov		[Font_SiteU4],	ax

		; Load di with destination offset and save it in DX
			mov		di,		ax	    ; AX retains it from prev action
			mov       dx,		di       ; Store it

		; Load ds:si with source
			lds		si,  [FONT_OFF]

		; Load es with SCREEN SEG
			mov		ax,  SCREEN_SEG
			mov		es,	ax

		; This section iterates for each char----------------------------
			mov		bx,	[NUMBER]	; Set BX to number of characters

		; Set BP to destination
			mov		bp,  dx        ; From above.

		; Set all data from CPU and non from latches
			mov		dx,	GC_INDEX
			mov		ax,	0FF00h + BIT_MASK  ; Set all CPU writes
			out		dx, 	ax

		; Set up DX plane selection
			mov		dx,	SC_INDEX

	@@BeginWrite:

		; Set AX up for plane selection
			mov		ax,	1102h	; Plane 0 selected

		; This section loops through the planes for each char -----------
	@@PlanesB:       			; Master plane loop.

			out	     dx,	ax	; Set the plane

		; Do the actual write.  MOVSB.  Move 6 single XBlocks on each line
			movsb  ; 1
			movsb  ; 2
			movsb  ; 3
			movsb  ; 4
			movsb  ; 5
			movsb  ; 6

	@@EndPlane:
		; Done with plane.  Reset Destination pointers.  Adjst plane
			mov		di,	bp
			shl		ah,	1		; Adjust plane. Carry set if done

		; !!End of Plane loop.
			jnc		@@PlanesB      ; If no carry, not done with char

		; !!End of Char loop.

			add		bp,	6		; Adjust destination for next char
			mov		di,	bp		; Store it in DI

			dec		bx             ; Another char done
			jnz		@@BeginWrite   ; If not zero, not done with font set

		; !!DONE!!
		@@DONE:
			pop		si di ds            ; Return state
			pop		bp
			ret

ENDP			_XRegister_Font4


; ----------------------------- _XChar4 ----------------------------------
; - This function will place a char from the current font to current screen
; - Uses the 8pix font
public		_XChar4

PROC			_XChar4   FAR

	ARG	X_S:WORD,	Y_S:WORD, CHAR:WORD

			push		bp
			mov		bp,	sp		; Save Stack frame
			push		ds di si

			cld

		; Load DS.
			ASSUME	ds:  @fardata
			mov		ax,  @fardata
			mov		ds,  ax			; Set DS to segment w/ Table

		; Set DI to start of character on screen
			mov 		ax,  [WLine_Offset]
			mul		[Y_S]		   ; Find Y offset value. Place in AX

			mov		di,  [X_S]        ; Find X offset value. Place in DI
			shr		di,  1
			shr		di,  1		   ; Adjust to number of XBlocks

			add		di,  ax		   ; Add X and Y offsets
			add		di,  [Write_Page] ; Add in page offset

		; Is the font uploaded or not
			mov		ax,	[UpLoaded4]
			cmp		ax,	UPLOADFONT
			je		@@FontIsUploaded	; The font is uploaded

	  ; - Font is not uploaded
		; Set CX to the scan line differential
			mov		cx,  [WLine_Offset]
			dec		cx			; Subtract the XBlock of a char

		; Figure character offset and add to load ds:si font site
			mov		ax,	24			; Put font size in ax
			mov		bx,	[CHAR]         ; Put character place in BX
			sub		bx,	[Char_Base4]	; Subtract base char
			mul		bx				; Mul size by place
			lds		si,  [Font_Addr4]
			add		si,  ax

		; Save the base destination in BX
			mov		bx,  di

		; Get the Screen Seg and put it in ES
			mov		ax,  SCREEN_SEG
			mov		es,	ax

		; Set all data from CPU and non from latches
			mov		dx,	GC_INDEX
			mov		ax,	0FF00h + BIT_MASK  ; Set all CPU writes
			out		dx, 	ax

		; Set up DX and AX for plane selection
			mov		ax,	1102h	; Plane 0 selected
			mov		dx,	SC_INDEX

		; This section is for WRITES ------------------------------

	@@PlanesW:          ; Master plane loop.

			out		dx,	ax	; Set the plane

	; Put a plane.  Completely un-rolled

		; Do line # 1
			movsb
			add		di,	cx		; Point DI to start of next line

		; Do line # 2
			movsb
			add		di,	cx		; Point DI to start of next line

		; Do line # 3
			movsb
			add		di,	cx		; Point DI to start of next line

		; Do line # 4
			movsb
			add		di,	cx		; Point DI to start of next line

		; Do line # 5
			movsb
			add		di,	cx		; Point DI to start of next line

		; Do line # 6
			movsb


		; Done with plane.  Reset Destination pointers.  Adjst plane
			mov		di,	bx		; Restore the destination start
			shl		ah,	1		; Adjust plane

		; !!End of Plane loop.
			jnc		@@PlanesW		; If no carry, then more to do.


		; Done with this
			jmp		@@DONE


	  ; - Font is uploaded
	  @@FontIsUploaded:
		; Calc the source and put in SI
			mov		si,	[Font_SiteU4]	; Get base location
			mov		ax,	6			; Put font size in ax
			xor		bx,	bx			; Clear bx
			mov		bx,	[CHAR]         ; Put character place in BX
			sub		bx,	[Char_Base4]	; Subtract base char
			mul		bx				; Mul size by place
			add		si,	ax			; Put in SI

		; Insure Map Mask is set to all planes
			mov		ax,  0F02h	; 0F sets all planes. 02 is Map Mask
			mov		dx,	SC_INDEX
			out		dx,	ax

		; Set all data from latches
			mov		dx,	GC_INDEX
			mov		ax,	00000h + BIT_MASK  ; Set all latch writes
			out		dx, 	ax

		; Set AX to line dif.
			mov		ax,	[WLine_Offset]		; Page width
			dec	     ax	                    ; minus XBlocks per char

		; Get the Screen Seg and put it in DS and ES
			mov		dx,  SCREEN_SEG
			mov		ds,  dx
			mov		es,	dx

		; This section is for WRITES ------------------------------

	  ; - Completely un-rolled

		; Scan line #1
			movsb
			add		di,	ax	; Point DI to start of next line

		; Scan line #2
			movsb
			add		di,	ax	; Point DI to start of next line

		; Scan line #3
			movsb
			add		di,	ax	; Point DI to start of next line

		; Scan line #4
			movsb
			add		di,	ax	; Point DI to start of next line

		; Scan line #5
			movsb
			add		di,	ax	; Point DI to start of next line

		; Scan line #6
			movsb


		; !!DONE!!
	@@DONE:
			pop		si di ds             ; Return state
			pop		bp
			ret

ENDP			_XChar4;


; ------------------------ _Register_Font_Masks4 ---------------------------
; - This function registers a font mask for the current 4pix font
; - This mask can be used for MFont character drawing
public		_Register_Font_Masks4

PROC			_Register_Font_Masks4   FAR

	ARG	MASK_OFF:WORD, MASK_SEG:WORD

			push		bp
			mov		bp,	sp		; Save Stack frame
			push		ds

		; Load DS.
			ASSUME	ds:  @fardata
			mov		ax,  @fardata
			mov		ds,  ax			; Set DS to fardata segment

		; Move location to local
			mov		ax,			[MASK_OFF]
			mov       [Font_Mask_O4], ax

			mov		ax,			[MASK_SEG]
			mov       [Font_Mask_S4], ax

		; !!DONE!!
			pop		ds
			pop		bp			; Return state
			ret

ENDP			_Register_Font_Masks4


; ----------------------------- _XChar4_M ----------------------------------
; - This function will place a char from the current font to current screen
; - This function requires a font mask set to be registered.
; - Uses the 4pix font
public		_XChar4_M

PROC			_XChar4_M   FAR

	ARG	X_S:WORD,	Y_S:WORD, CHAR:WORD


			push		bp
			mov		bp,	sp			; Save Stack frame
			push		ds di si

			cld

		; Load DS.
			ASSUME	ds:  @fardata
			mov		ax,  @fardata
			mov		ds,  ax			; Set DS to fardata segment

		; Set DI to start of rectangle on screen
			mov 		ax,  [WLine_Offset]
			mov		bx,	ax		   ; Save in BX as S/L differential
			mul		[Y_S]		   ; Find Y offset value. Place in AX

			mov		di,  [X_S]        ; Find X offset value. Place in DI
			shr		di,  1
			shr		di,  1

			add		di,  ax		   ; Add X and Y offsets
			add		di,  [Write_Page] ; Add in page offset

		; Is the font uploaded or not
			mov		cx,	[UpLoaded4]  ; UPLOADED is 0!
			jcxz		@@Uploaded	   ; The font is uploaded

	  ; - Font is not uploaded

		; Figure character offset and add to load ds:si font site
			mov		ax,	24			; Put font size in ax
			mov		cx,	[CHAR]         ; Put character place in BX
			sub		cx,	[Char_Base4]	; Subtract base char
			mul		cx				; Mul size by place
			lds		si,  [Font_Addr4]
			add		si,  ax

		; Save the base destination in CX
			mov		cx,  di

		; Get the Screen Seg and put it in ES
			mov		ax,  SCREEN_SEG
			mov		es,	ax

		; Set all data from CPU and non from latches
			mov		dx,	GC_INDEX
			mov		ax,	0FF00h + BIT_MASK  ; Set all CPU writes
			out		dx, 	ax

		; Set up DX and AX for plane selection
			mov		ax,	0102h	; Plane 0 selected
			mov		dx,	SC_INDEX

		; This section is for WORD WRITES ------------------------------

	@@PlanesW:          ; Master plane loop.

			out		dx,	ax	; Set the plane


	  ; Do the actual DRAW.  MOV.  Completely un-rolled!!

		; Scan line #1
			lodsb	; Get the pixel
			or		al,	al		; Is it zero
			jz		@@NoDraw1       ; If zero, don't draw it
			mov		[es:di],	al	; Else, do draw it
	@@NoDraw1:
			add		di,	bx		; Point DI to start of next line

		; Scan line #2
			lodsb	; Get the pixel
			or		al,	al		; Is it zero
			jz		@@NoDraw2       ; If zero, don't draw it
			mov		[es:di],	al	; Else, do draw it
	@@NoDraw2:
			add		di,	bx		; Point DI to start of next line

		; Scan line #3
			lodsb	; Get the pixel
			or		al,	al		; Is it zero
			jz		@@NoDraw3       ; If zero, don't draw it
			mov		[es:di],	al	; Else, do draw it
	@@NoDraw3:
			add		di,	bx		; Point DI to start of next line

		; Scan line #4
			lodsb	; Get the pixel
			or		al,	al		; Is it zero
			jz		@@NoDraw4       ; If zero, don't draw it
			mov		[es:di],	al	; Else, do draw it
	@@NoDraw4:
			add		di,	bx		; Point DI to start of next line

		; Scan line #5
			lodsb	; Get the pixel
			or		al,	al		; Is it zero
			jz		@@NoDraw5       ; If zero, don't draw it
			mov		[es:di],	al	; Else, do draw it
	@@NoDraw5:
			add		di,	bx		; Point DI to start of next line

		; Scan line #6
			lodsb	; Get the pixel
			or		al,	al		; Is it zero
			jz		@@NoDraw6       ; If zero, don't draw it
			mov		[es:di],	al	; Else, do draw it
	@@NoDraw6:


		; Done with plane.  Reset Destination pointers.  Adjst plane
			mov		di,	cx		; Restore the destination start

			mov		al,  02h		; Restore MAP MASK pointer
			shl		ah,	1		; Adjust plane

		; !!End of Plane loop. If carry not set, then not done
			jnc		@@PlanesW

		; Done with this
			jmp		@@DONE


	@@Uploaded:
	  ; - Font is uploaded
		; Set SI to mask offset
			mov		ax,	3			; Put mask size in ax
			mov		cx,	[CHAR]         ; Put character place in CX
			sub		cx,	[Char_Base4]	; Subtract base char
			mul		cx				; Mul size by place
			add		ax,	[Font_Mask_O4] ; Find masks offset in mask set
			mov		si,	ax			; Save it

		; Calc the source and put in BX
			mov		bx,	[Font_SiteU4]	; Get base location
			mov		ax,	6			; Put font size in ax
			mov		cx,	[CHAR]         ; Put character place in BX
			sub		cx,	[Char_Base4]	; Subtract base char
			mul		cx				; Mul size by place
			add		bx,	ax			; Put in SI

		; Set BP to the scan line differential
			mov		bp,	[WLine_Offset]

		; Set DS to mask area
			mov		ax,	[Font_Mask_S4]
			mov 		ds,	ax

		; Get the Screen Seg and put it in ES
			mov		ax,  SCREEN_SEG
			mov		es,	ax

		; Set all data from latches
			mov		dx,	GC_INDEX
			mov		ax,	00000h + BIT_MASK  ; Set all latch writes
			out		dx, 	ax

		; Set up Map Mask
			mov		al,  02h	; 02 is Map Mask
			mov		dx,	SC_INDEX
			out		dx,	al
			inc		dx

		; Set CL to shift spaces
			mov		cl,	4

	 ; This section is for WRITES ------------------------------

	 ; -- Completely unrolled

	  ; Scan Line #1
		; This section prep the Map Mask
			lodsb				; Get mask byte

		; Set mask for first write
			out		dx,	al		; Set map mask

		; Write the byte
			mov		ah,		[es:bx]
			mov		[es:di],	ah

	  ; Scan Line #2
		; Set mask for next write
			shr		al,       cl		; Move upper nibble down
			out		dx,		al        ; Set map mask

		; Adjust for next scan line
			add		di,	bp	; Point DI to start of next line
			inc		bx

		; Write the second byte
			mov		al,		[es:bx]
			mov		[es:di],	al

		; Adjust for next scan line
			add		di,	bp	; Point DI to start of next line
			inc		bx

	  ; Scan Line #3
		; This section prep the Map Mask
			lodsb				; Get mask byte

		; Set mask for first write
			out		dx,	al		; Set map mask

		; Write the byte
			mov		ah,		[es:bx]
			mov		[es:di],	ah

	  ; Scan Line #4
		; Set mask for next write
			shr		al,       cl		; Move upper nibble down
			out		dx,		al        ; Set map mask

		; Adjust for next scan line
			add		di,	bp	; Point DI to start of next line
			inc		bx

		; Write the second byte
			mov		al,		[es:bx]
			mov		[es:di],	al

		; Adjust for next scan line
			add		di,	bp	; Point DI to start of next line
			inc		bx

	  ; Scan Line #5
		; This section prep the Map Mask
			lodsb				; Get mask byte

		; Set mask for first write
			out		dx,	al		; Set map mask

		; Write the byte
			mov		ah,		[es:bx]
			mov		[es:di],	ah

	  ; Scan Line #6
		; Set mask for next write
			shr		al,       cl		; Move upper nibble down
			out		dx,		al        ; Set map mask

		; Adjust for next scan line
			add		di,	bp	; Point DI to start of next line
			inc		bx

		; Write the second byte
			mov		al,		[es:bx]
			mov		[es:di],	al


		; !!DONE!!
	@@DONE:
			pop		si di ds             ; Return state
			pop		bp
			ret

ENDP			_XChar4_M;


;----------------------------- _XString4 ----------------------------------
; - This function will place a char from the current font to current screen
; - It will use the masked font on mask flag set to not 0.
public		_XString4

PROC			_XString4   FAR

	ARG	X_S:WORD,	Y_S:WORD, MASKIT:WORD, STR_OFF:DWORD

			push		bp
			mov		bp,	sp		; Save Stack frame
			push		ds di si

		; Load DS.
			ASSUME	ds:  @fardata
			mov		ax,  @fardata
			mov		ds,  ax			; Set DS to segment w/ Table

		; Move flag into CX
			mov		cx,  [MASKIT]

		; Load DS:SI
			lds		si,  [STR_OFF]

		; !!! This is the master string loop
	@@STRING:
			xor       ax,	ax
			mov	  	al,	[ds:si]		; Get the char
			cmp		al,	0			; Is it the EOS
			je		@@DONE			; If so jump

		 ; Save cx
			push 	cx

		 ; Build stack frame
			push		ax				; Push the char
			push		[Y_S]			; Push the Y coor
			push		[X_S]			; Push the X coor
		 ; To mask or not to mask
			jcxz		@@DontMask		; If Flag = 0, dont mask
			call		_XChar4_M			; Put the char
			jmp		@@Continue

	@@DontMask:
			call      _XChar4			; Do a mask instead

	@@Continue:
			add		sp,	6			; Adjust the stack

			pop		cx				; Restore cx

			add		[X_S],	4		; Move the cursor
			inc		si				; Point to next char
			jmp		@@STRING            ; Continue

		; !!DONE!!
	@@DONE:
			pop		si di ds             ; Return state
			pop		bp
			ret

ENDP			_XString4


; --------------------------- _XPaste_Tile_M -------------------------------
; - This function will place a tile from the display memory to current screen
; - It will use the mask passed to it to leave background transparent
; - Max Y size is 255

public		_XPaste_Tile_M

PROC			_XPaste_Tile_M   FAR

 ARG	X_S:WORD,	Y_S:WORD, X_D:WORD,	Y_D:WORD,	TILE:WORD, MASK_O:DWORD

			push		bp
			mov		bp,	sp			; Save Stack frame
			push		ds di si

               cld
 
		; Load DS.
			ASSUME	ds:  @fardata
			mov		ax,  @fardata
			mov		ds,  ax			; Set DS to fardata segment

		; Set DI to start of rectangle on screen
			mov 		ax,  [WLine_Offset]
			mul		[Y_S]		   ; Find Y offset value. Place in AX

			mov		di,  [X_S]        ; Find X offset value. Place in DI
			shr		di,  1
			shr		di,  1		   ; Adjust to number of XBlocks

			add		di,  ax		   ; Add X and Y offsets
			add		di,  [Write_Page] ; Add in page offset

		; Put in source in BX
			mov		bx,	[TILE]

		; Get the Screen Seg and put it in ES
			mov		ax,  SCREEN_SEG
			mov		es,	ax

		; Set all data from latches
			mov		dx,	GC_INDEX
			mov		ax,	00000h + BIT_MASK  ; Set all latch writes
			out		dx, 	ax

		; Set CX to the number of XBlocks per line
			mov		cx,	[X_D]
			shr		cx,	1
			shr		cx,	1

		; Get distance from end of one line to start of other and save
			mov		ax,		[WLine_Offset]	; Get screen width
			sub		ax,		cx			; Del number of XBlocks
			mov		dx,       ax			; Save in DX

		; Save cl in ch.
			mov		ch,  cl	; Save in CH


		; Set DS:SI to mask area
			lds		si,	[MASK_O]

		; Set AH to the rows to do
			mov		ax,	[Y_D]	; Puts number in AX
			mov		ah,	al		; Puts number in ah

		; Move scan line differential from dx to bp
			mov		bp,  dx

		; Set up Map Mask
			mov		al,  02h	; 02 is Map Mask
			mov		dx,	SC_INDEX
			out		dx,	al
			inc		dx

		; This section is for WRITES ------------------------------
	@@Rowloop:

	@@ScanLine:
		; This section prep the Map Mask
			lodsb				; Get mask byte

		; Set mask for write
			out		dx,	al		; Set map mask

		; Write the XBlock
			mov		al,		[es:bx]
			mov		[es:di],	al

		; Adjust bx and si
			inc		bx
			inc		di

		; End of ScanLine
			dec		cl
			jnz		@@ScanLine

		; Adjust for next iteration
			add		di,	bp	; Point DI to start of next line

		; Restore cl
			mov		cl,  ch

		; End of Rowloop.
			dec		ah
			jnz		@@Rowloop

		; !!DONE!!
	@@DONE:
			pop		si di ds             ; Return state
			pop		bp
			ret

ENDP			_XPaste_Tile_M;


; --------------------------- _XPaste_Sprite_M ----------------------------
; - This function will place a sprite from the display memory to current screen
; - It will use the mask passed to it to leave background transparent
; - Max Y size is 255
public		_XPaste_Sprite_M

PROC			_XPaste_Sprite_M   FAR

 ARG	X_S:WORD,	Y_S:WORD, X_D:WORD,	Y_D:WORD,	TILE:WORD, MASK_O:DWORD

			push		bp
			mov		bp,	sp			; Save Stack frame
			push		ds di si

               cld
 
		; Load DS.
			ASSUME	ds:  @fardata
			mov		ax,  @fardata
			mov		ds,  ax			; Set DS to fardata segment

		; Set all data from latches
			mov		dx,	GC_INDEX
			mov		ax,	00000h + BIT_MASK  ; Set all latch writes
			out		dx, 	ax

		; Set DI to start of rectangle on screen
			mov 		ax,  [WLine_Offset] ; Scan line size
			mul		[Y_S]		   ; Find Y offset value. Place in AX

			mov		di,  [X_S]        ; Find X offset value. Place in DI
			shr		di,  1
			shr		di,  1		   ; Adjust to number of XBlocks

			add		di,  ax		   ; Add X and Y offsets
			add		di,  [Write_Page] ; Add in page offset

		; Put source in BX
			mov		bx,	[TILE]

		; Find adjustment to source for alignment
			mov		ax,	[X_D]
			mov		cx,	[X_S]

			shr		ax,	1	   ; Number of XBlocks a line
			shr		ax,	1
			mul		[Y_D]        ; Find size of each sprite alignment

			and		cx,	3	   ; Mask all but 2 LSBs

			mul		cx		   ; Find new offset

			add		bx,	ax	   ; Adjust BX

		; Save adjustment for mask for alignment in DX
			mov		dx,	ax	   ; AX as calculated above

		; Set CX to the number of XBlocks
			mov		cx,	[X_D]
			shr		cx,	1
			shr		cx,	1

		; Get distance from end of one line to start of other and save
			mov		ax,	[WLine_Offset]	; Get screen width
			sub		ax,	cx			; Del number of XBlocks

		; Set DS:SI to mask area
			lds		si,	[MASK_O]

		; Add alignment adjust to si
			add		si,  dx

		; Set AH to the rows to do
			mov		dx,	[Y_D]	; Puts number in AX
			mov		bp,	ax		; Move diff into BP
			mov		ah,	dl		; Puts number in ah

		; Get the Screen Seg and put it in ES
			mov		dx,  SCREEN_SEG
			mov		es,	dx

		; Set up Map Mask
			mov		al,  02h	; 02 is Map Mask
			mov		dx,	SC_INDEX
			out		dx,	al
			inc		dx

		; Save scan length (CL) in CH
			mov		ch,	cl

		; This section is for WRITES ------------------------------
	@@Rowloop:

	@@ScanLine:
		; This section prep the Map Mask
			lodsb				; Get mask byte

		; Set mask for write
			out		dx,	al		; Set map mask

		; Write the XBlock
			mov		al,		[es:bx]
			mov		[es:di],	al

		; Adjust bx and si
			inc		bx
			inc		di

		; End of ScanLine
			dec		cl
			jnz		@@ScanLine


		; Adjust for next iteration
			add		di,	bp	; Point DI to start of next line

		; Restore scan length
			mov		cl,  ch

		; End of Rowloop.
			dec		ah
			jnz		@@Rowloop

		; !!DONE!!
	@@DONE:
			pop		si di ds             ; Return state
			pop		bp
			ret

ENDP			_XPaste_Sprite_M;


; ------------------------ _XSet_Pal_Color ---------------------------
; - This function uses the bios to set a single color in the palette
; -
public		_XSet_Pal_Color

PROC			_XSet_Pal_Color

	ARG	COLOR:WORD, RED:WORD, GREEN:WORD, BLUE:WORD

			push		bp
			mov		bp,	sp		; Save Stack frame

			mov		bx,	[COLOR]	; Load color number

			mov		ax,	[RED]
			mov		dh,	al		; Load Red value

			mov		ax,	[GREEN]
			mov		ch,	al		; Load Green value

			mov		ax,	[BLUE]
			mov		cl,	al		; Load Blue value

			mov		ax,	01010h	; Load ax with BIOS routine id
			int		010h			; Call BIOS

			pop		bp			; Return state
               ret

ENDP			_XSet_Pal_Color

; ------------------------ _XSet_Pal_Block ---------------------------
; - This function uses the bios to set a block of colors in the palette
; -
public		_XSet_Pal_Block

PROC			_XSet_Pal_Block

	ARG	START:WORD, NUMBER:WORD, PAL_O:DWORD

			push		bp
			mov		bp,	sp		; Save Stack frame

			mov		bx,	[START]	; Load start color number

			mov		cx,	[NUMBER]  ; Load number of colors

			les		dx,	[PAL_O]   ; Load pointer data

			mov		ax,	01012h	; Load ax with BIOS routine id
			int		010h			; Call BIOS

			pop		bp			; Return state
               ret

ENDP			_XSet_Pal_Block


; ------------------------ _XPut_Pixel ------------------------------------
; - Put a pixel to the screen.
; -
public		_XPut_Pixel

PROC			_XPut_Pixel

	ARG	X_LOC:WORD, Y_LOC:WORD, COLOR:WORD

			push		bp
			mov		bp,	sp		; Save Stack frame

			push		ds di 


		; Load DS.
			ASSUME	ds:  @fardata
			mov		ax,  @fardata
			mov		ds,  ax			; Set DS to fardata segment

		; Set DI to the XBlock
			mov 		ax,  [WLine_Offset]  ; Scan line size
			mul		[Y_LOC]		   ; Find Y offset value. Place in AX

			mov		di,  [X_LOC]      ; Find X offset value. Place in DI
			mov       cx,	di		   ; For later to determine the mask
			shr		di,  1
			shr		di,  1		   ; Adjust to number of XBlocks

			add		di,  ax		   ; Add X and Y offsets
			add		di,  [Write_Page] ; Add in page offset


		; Set ah to the mask
			and		cx,	03h		; Get the lower 2 bits
			mov		ax,	0102h     ; MAP MASK
			shl		ah,	cl		; Shift to app. plane

		; Set up Map Mask
			mov		dx,	SC_INDEX
			out		dx,	ax

		; Set all data from CPU and non from latches
			mov		dx,	GC_INDEX
			mov		ax,	0FF00h + BIT_MASK  ; Set all CPU writes
			out		dx, 	ax

		; Get the Screen Seg and put it in DS
			mov		ax,  SCREEN_SEG
			mov		ds,	ax

		; Get the color
			mov		ax,	[COLOR]	

		; Put the Pixel
			mov		[di],	al

		; !!DONE!!
			pop		di ds             ; Return state
			pop		bp
			ret

ENDP  		_XPut_Pixel


; ------------------------ _MEvent_Handler ---------------------------
; - Xtile's mouse event handler
; -
PROC			_MEvent_Handler

		; Save thier DS
			push ds di si

	; This function is not callable from 'c' so we won't build a
	; stack frame

		; Save the event masks
			push 	ax

		; Correct X pos
			shr	cx,	1

		; Save dx in SI
			mov		si,	dx

		; Load DS.
			ASSUME	ds:  @fardata
			mov		ax,  @fardata
			mov		ds,  ax			; Set DS to fardata segment

		; Check for Lock
			cmp		[LockHandler],	NOTLOCKM	; Is it locked
			jz		@@NotLocked

		; !!!Locked!!!  Jump out
			pop		ax					; Pull flags off stack
			jmp 		@@Done

	@@NotLocked:
		; Lock the handler
			mov		ax,	LOCKM
			mov		[LockHandler], ax

	; Get and save the status of bit mask and map mask
		; Get status of bit mask
			mov		dx,	GC_INDEX
			mov		ax,	0808h     ; 08 is bit mask
			out		dx, 	al
			inc		dx
			in		al,	dx		; Get the bit mask
			xchg		al,	ah
			push		ax

		; Get Map Mask
			mov		ax,  0202h		; 02 is Map Mask
			mov		dx,	SC_INDEX
			out		dx,	al
			inc		dx
			in		al,	dx
               xchg		al,  ah
			push  	ax

	; -- Restore the the current background location ---
	; This is a Save Site restore
			cld

		; Set DI to start of rectangle on screen
			mov 		ax,  [DLine_Offset] ; Get line size
			mov		bx,  [MLastY]       ; Get Y into view
			push      bx				; Save on stack
			add	     bx,  [ViewYLoc]     ; Add start of view
			mul		bx			   ; Find Y offset value. Place in AX

			mov		di,  [MLastX]     ; Find X offset value. Place in DI
			mov		bp,  di		   ; Save X in BP
			add		di,  [ViewXLoc]   ; Get start of view and add it
			shr		di,  1
			shr		di,  1

			add		di,  ax		     ; Add X and Y offsets
			add		di,  [Display_Page] ; Add in page offset

		; Get DX back
			mov		dx,	si

		; Save the new location as the last known
			mov		[MLastY],	dx
			mov		[MLastX],	cx

		; See if we need to clip Second XBlock
			mov		cx,		2			; Assume 2 XBlocks to write
			mov		dx,       [ScreenSizeX]	; Get screen size
			sub		dx,		4			; Get 2nd to last XBlock
			cmp		dx,		bp
			jae       @@Continue			; Both blocks on reload
			dec		cx					; Only one block on reload

	@@Continue:
		; Set all data from latches
			mov		dx,	GC_INDEX
			mov		ax,	00000h + BIT_MASK  ; Set all latch writes
			out		dx, 	ax

		; Insure Map Mask is set to all planes
			mov		ax,  0F02h	; 0F sets all planes. 02 is Map Mask
			mov		dx,	SC_INDEX
			out		dx,	ax

		; Set DX to number of lines to display
			mov		dx,	8		; Assume 8 scan lines
			pop		bx			; Get Y last and put in BX
			mov		ax,	[ScreenSizeY] ; Get Screen size Y and put in ax
			sub		ax,	8		; Subtract to make mimimum clip

			cmp		ax,	bx		; Does it need clipping?
			ja		@@ContinueC	; No, then go!

			sub		bx,  ax		; Needs clipping!  Find new DX
			sub		dx,	bx

	@@ContinueC:
		; Set BP to the distance from one scan line to next
			mov		bp,	[DLine_Offset]
			sub		bp,  cx    		; # XBlocks per scan line

		; Get the Screen Seg and put it in DS and ES
			mov		ax,  SCREEN_SEG
			mov		ds,  ax
			mov		es,	ax

		; Set SI to source
			mov		si,		MSAVEOFFSET

		; Save number to move (CX) in BX
			mov		bx,	cx

	   ; This section is for WRITES ------------------------------
	   ; The Reload will be clipped if appropriate

	 @@RowLoop:
		 ; Put 1 or 2 XBlocks
			rep movsb

		 ; Ready next iteration
			add		di,	bp	; Point DI to start of next line

		 ; Reload CX
			mov		cx,	bx

		 ; Are we done?
			dec		dx
			jnz		@@RowLoop	; No, then go and do another line


	; ------ Now save the new area --------
	; -- Do the reload

		; Load DS.
			ASSUME	ds:  @fardata
			mov		ax,  @fardata
			mov		ds,  ax			; Set DS to fardata segment

		; Set SI to start of rectangle on screen
			mov 		ax,  [DLine_Offset] ; Get line size
			mov		bx,  [MLastY]       ; Get Y into view
			mov		di,	bx			; Save in DI
			add	     bx,  [ViewYLoc]     ; Add start of view
			mul		bx			   ; Find Y offset value. Place in AX

			mov		si,  [MLastX]     ; Find X offset value. Place in DI
			mov		bp,  si		   ; Save in BP for later
			add		si,  [ViewXLoc]   ; Get start of view and add it
			shr		si,  1
			shr		si,  1

			add		si,  ax		     ; Add X and Y offsets
			add		si,  [Display_Page] ; Add in page offset

			push		si				; Save what will later be DI

		; See if we need to clip Second XBlock
			mov		cx,		2			; Assume 2 XBlocks to write
			mov		dx,       [ScreenSizeX]	; Get screen size
			sub		dx,		4			; Get 2nd to last XBlock
			cmp		dx,		bp             ; BP from above.
			jae        @@Continue3			; Both blocks on reload
			dec		cx					; Only one block on reload

	@@Continue3:
		; Set DX to number of lines to reload
			mov		dx,	8		; Assume 8 scan lines
			mov		bx, 	di		; Get Y last and put in BX (from SI)
			mov		ax,	[ScreenSizeY] ; Get Screen size Y and put in ax
			sub		ax,	8		; Subtract to make mimimum clip

			cmp		ax,	bx		; Does it need clipping?
			ja		@@Continue4	; No, then go!

			sub		bx,  ax		; Needs clipping!  Find new DX
			sub		dx,	bx

	@@Continue4:
		; Save DX ( as number of lines to do ) for later
			push		dx

		; Set BP to scan line dif.
			mov		bp,	[DLine_Offset]
			sub		bp,  cx

		; Get the Screen Seg and put it in DS
			mov		ax,  SCREEN_SEG
			mov		ds,	ax

		; Set destination
			mov		di,	MSAVEOFFSET

		; Save SI so we dont have to calc later
			mov		ax,	si

		; Save CX in BX
			mov		bx,	cx

	   ; This section is for the MOVE ------------------------------

	@@RowLoop2:
		; Do line
			rep		movsb	; One or two XBlocks

		; Prep for next line
			add		si,	bp	; Point SI to start of next line
			mov		cx,	bx	; Restore CX

		; Are we done?
			dec		dx
			jnz		@@RowLoop2	; No, then go and do another


	; -------- Put the pointer in the new position ---------

		; Load DS.
			ASSUME	ds:  @fardata
			mov		ax,  @fardata
			mov		ds,  ax			; Set DS to fardata segment


		; Find adjustment to source for alignment
			mov		bx,	[MLastX]	 ; Put X loc into cx
			add		bx,  [ViewXLoc] ; Add view start
			mov		ax,	16		; Alignment size ( 2XBlocks*8Scan )

			and		bx,	3	   ; Mask all but 2 LSBs
			mul		bx		   ; Find offset into alignments (Put in AX)

		; Put source into BX and add the alignment adj
			mov		bx,	MOUSEOFFSET
			add		bx,	ax	   ; Adjust BX


		; Set DS:SI to mask area
			lds		si,	[MPointerMask]

		; Add adjustment for mask for alignment
			add		si,	ax	   ; AX as calculated above

		; Set ah to rows to do
			pop		ax		   ; Was DX in the reload
			mov		ah,  al	   ;	Move to upper nibble

		; Increment BP to account for no last inc
			inc		bp

		; Pop the destination, prev si, from the stack
			pop		di

		; Set up Map Mask
			mov		al,  02h	; 02 is Map Mask
			mov		dx,	SC_INDEX
			out		dx,	al
			inc		dx

		; Shift CX down to see if we need to write 2nd XBlock
			shr		cx,	1

		; This section is for WRITES ------------------------------
	@@Rowloop3:


		; Set mask for first XBlock write
			lodsb				; Get mask byte
			out		dx,	al		; Set map mask

		; Write the 1st XBlock
			mov		al,		[es:bx]
			mov		[es:di],	al

		; Load the next mask
			lodsb

		; Increment source pointers
			inc	bx

		; If CX is zero, pointer is clipped.  Don't put next XBlock
			jcxz		@@Clipped

	   ; - Do another write
		; Set the mask
			out		dx,	al		; Set map mask

		; Increment destination pointers
			inc	di

		; Write the 2nd XBlock
			mov		al,		[es:bx]
			mov		[es:di],	al

	 @@Clipped:
		; Adjust for next iteration
			add		di,	 bp  ; Point to next scan line
			inc		bx

		; End of Rowloop.
			dec		ah
			jnz		@@Rowloop3

	  ; -- Done with screen

	  ; Restore the state of the bit mask and map mask
		; Get Map Mask
			mov		dx,	SC_INDEX
			pop		ax
			out		dx,	ax

		; Do the BIT MASK
			mov		dx,	GC_INDEX
			pop		ax
			out		dx, 	ax

	  ; -- See if we have to call the user's handler

		; Make ES the local data
			ASSUME	es:  @fardata
			mov		ax,  @fardata
			mov		es,  ax			; Set ES to fardata segment

		; Restore event masks
			pop		ax

		; AND the mask with users mask
			and		ax,	[es:MUEventMask]

		; If zero then don't call user's
			je		@@Done

		; Load users DGROUP
			mov	ax,  @data
			mov  ds,  ax

		; Otherwise set up stack and call users routine
			push		ax		; Push the event mask

			push		[es:MLastY]  ; Push Y location

			push		[es:MLastX]  ; Push X location

			call   	[es:Mouse_Hndlr_O]	; User pointer to call user handler

					; Clean stack after call
			add		sp,	6


	  ; -- DONE!!

	@@Done:
		; Make DS the local data
			ASSUME	ds:  @fardata
			mov		ax,  @fardata
			mov		ds,  ax			; Set DS to fardata segment

		; Unlock the handler
			mov		ax,	NOTLOCKM
			mov		[LockHandler], ax

			pop   si di ds     ; Pop mouses DS
			ret

ENDP			_MEvent_Handler


; ------------------------ _XRegister_Mouse ---------------------------
; - This function sets up the use of the mouse
; - It returns FALSE (0) if it cannot
; -
public		_XRegister_Mouse

PROC			_XRegister_Mouse

	ARG	MPOINT:DWORD, MMASK:DWORD, HANDLER:DWORD

			push		bp
			mov		bp,	sp		; Save Stack frame

		; Save registers
			push		ds si di

		; First see if mouse is available
			mov       ax,	0
			int		33h

			cmp		ax,	0FFFFh	; If ax is FFFFh then mouse is present
			je 		@@Cont

			mov		ax,	MOUSE_NOTPRES
			jmp		@@Done		; Otherwise, done

		; Mouse is present so continue
	@@Cont:

	; ----------- Upload mouse pointer ------------

		; Prep the stack for call to upload sprite
			les		si,	[MPOINT]	; Get the image offset
			push		es
			push		si

			mov		si,	(64*4)		; Mouse sprite size
			push		si

			mov		si,	MOUSEOFFSET    ; Mouse pointer site
			push		si

		; Call upload sprite
			call 	_XUpload_Sprite

		; Fix the stack after call
			add		sp,	8

	; ---- Done uploading mouse pointer


		; Load DS.
			ASSUME	ds:  @fardata
			mov		ax,  @fardata
			mov		ds,  ax			; Set DS to fardata segment

		; Get the pointer mask address
			les		si,  [MMASK]
			mov		[MPointerMask_O], si
			mov		ax,  es
			mov		[MPointerMask_S], ax

		; Set up user handler
			les		si,  [HANDLER]
			mov		[OFFSET Mouse_Hndlr_O], si
			mov		ax,  es
			mov		[OFFSET Mouse_Hndlr_O+2], ax

		; !!DONE!!
			mov		ax,	MOUSE_PRESENT   ; Return the mouse is present


		; Set the new mickey counts
		 	mov		ax,	000Fh		 ; Mouse driver routine
			mov		cx,  8			 ; Double horizontal
			mov		dx,  8			 ; Default vertical
			int		033h				 ; Call routine	

	@@Done:
			pop		di si ds

			pop		bp			; Return state
               ret

ENDP			_XRegister_Mouse


; ------------------------ _Mouse_Active ---------------------------
; - Makes the mouse active and starts display of the pointer
; -
public		_XMouse_Active

PROC			_XMouse_Active

	ARG	EMASK:WORD, STARTX:WORD, STARTY:WORD

			push		bp
			mov		bp,	sp		; Save Stack frame

		; Save regs
			push		ds si di

		; Load DS.
			ASSUME	ds:  @fardata
			mov		ax,  @fardata
			mov		ds,  ax			; Set DS to fardata segment

			cld

		; Save the users mask
			mov		cx,	[EMASK]   	; Get user's mask
			push		cx

		; Set mouse reporting limits
			mov		ax,  0007h	; Set X limits function
			mov		cx,	0000h	; X minimum
			mov       dx,  [ScreenSizeX]       ; X maximum
			dec		dx
			shl		dx,	1	; Comp for oddity
			int		033h

			mov		ax,	0008h	; Set Y limits function
			mov		cx,	0000h     ; Y minimum
			mov		dx,	[ScreenSizeY]		; Y maximum
			dec		dx  			; Adjust for pointer size
			int       033h

	; -- Set up VGA Registers
		; Set all data from latches
			mov		dx,	GC_INDEX
			mov		ax,	00000h + BIT_MASK  ; Set all latch writes
			out		dx, 	ax

		; Insure Map Mask is set to all planes
			mov		ax,  0F02h	; 0F sets all planes. 02 is Map Mask
			mov		dx,	SC_INDEX
			out		dx,	ax

	; -- Set the mouse's position and display the pointer

		; Let the driver know where the mouse pointer is
			mov		ax,	0004h	; Set mouse pointer position func

			mov		cx,	[STARTX]	; Set X coord
			mov		dx,	[STARTY]	; Set Y coord

			mov       [MLastX], cx   ; Save as last location
			mov		[MLastY], dx

			shl		cx,	1		; Comp

			int		033h			; Call function

		; Reload background & fake a mouse event to put the pointer
		; on the screen the first time

	; -- Do the reload

		; Return CX to actual coord
			shr		cx,	1

		; Set SI to start of rectangle on screen
			mov 		ax,  [DLine_Offset] ; Get line size
			mov		bx,  [MLastY]       ; Get Y into view
			mov		di,	bx			; Save in DI
			add	     bx,  [ViewYLoc]     ; Add start of view
			mul		bx			   ; Find Y offset value. Place in AX

			mov		si,  [MLastX]     ; Find X offset value. Place in DI
			mov		bp,  si		   ; Save in BP for later
			add		si,  [ViewXLoc]   ; Get start of view and add it
			shr		si,  1
			shr		si,  1

			add		si,  ax		     ; Add X and Y offsets
			add		si,  [Display_Page] ; Add in page offset

		; See if we need to clip Second XBlock
			mov		cx,		2			; Assume 2 XBlocks to write
			mov		dx,       [ScreenSizeX]	; Get screen size
			sub		dx,		4			; Get 2nd to last XBlock
			cmp		dx,		bp             ; BP from above.
			jge       @@Continue3			; Both blocks on reload
			dec		cx					; Only one block on reload

	@@Continue3:
		; Set DX to number of lines to reload
			mov		dx,	8		; Assume 8 scan lines
			mov		bx, 	di		; Get Y last and put in BX (from SI)
			mov		ax,	[ScreenSizeY] ; Get Screen size Y and put in ax
			sub		ax,	8		; Subtract to make mimimum clip

			cmp		ax,	bx		; Does it need clipping?
			jg		@@Continue4	; No, then go!

			sub		bx,  ax		; Needs clipping!  Find new DX
			sub		dx,	bx

	@@Continue4:

		; Set BP to scan line dif.
			mov		bp,	[DLine_Offset]
			sub		bp,  cx

		; Get the Screen Seg and put it in DS and ES
			mov		ax,  SCREEN_SEG
			mov		ds,	ax
			mov		es,  ax

		; Set destination
			mov		di,	MSAVEOFFSET

		; Save CX in BX
			mov		bx,	cx

	   ; This section is for the MOVE ------------------------------
	   ; Completely unrolled

	@@RowLoop2:
		; Do line
			rep		movsb	; One or two XBlocks

		; Prep for next line
			add		si,	bp	; Point SI to start of next line
			mov		cx,	bx	; Restore CX

		; Are we done?
			dec		dx
			jnz		@@RowLoop2	; No, then go and do another

	  ; - Done with Reload

		; Load DS.
			ASSUME	ds:  @fardata
			mov		ax,  @fardata
			mov		ds,  ax			; Set DS to fardata segment

		; Ensure the mouse is not locked
			mov		ax,			NOTLOCKM
			mov		[LockHandler], ax

		; Fake a mouse event and call handler
			mov		ax,	0001h	; Indicate a move
			mov		bx,	0000h	; No info
			mov		cx,  [MLastX]  ; X coord
			shl		cx, 	1		; Double, that's how it is expected
			mov		dx,	[MLastY]  ; Y coord

			call		_MEvent_Handler

		; Get the users event mask, save, and build our mask in cx
			pop		cx				; Get the Mask back
			mov		[MUEventMask], cx   ; Save it
			or		cx,  0001h          ; Build ours
						; We must make sure all moves are reported

		; Define XTile's handler and register it with the mouse driver
			mov		ax,	0000Ch	; Function number

			mov		dx,	cs		; Get XTile's handler address
			mov		es,	dx
			mov		dx,  OFFSET _MEvent_Handler

			int		033h			; Call the mouse driver
		; !!DONE!!

			pop		di si ds		; Restore regs

			pop		bp			; Return state
			ret

ENDP			_XMouse_Active


; ------------------------ _XMouse_InActive ---------------------------
; - Turns off the mouse.  Restores background to last known mouse position.
; -
public		_XMouse_InActive

PROC			_XMouse_InActive

			push		bp
			mov		bp,	sp		; Save Stack frame

		; Save regs
			push		ds si di

		; Register with mouse driver that we don't want any more reports
			mov		ax,	0000Ch	; Function number
			mov		cx,	00000h	; Clear flag
			mov		dx,	00000h	; Get XTile's handler address
			mov		es,	dx
			mov		dx,  00000h

			int		033h			; Call the mouse driver

		; Load DS with local segment
			ASSUME	ds:  @fardata
			mov		ax,  @fardata
			mov		ds,  ax		; Set DS to fardata segment

		; Ensure the mouse is locked
			mov		ax,			LOCKM
			mov		[LockHandler], ax

		; Clear the users mask
			mov		cx,	0000h   		; Set to no events
			mov		[MUEventMask], cx   ; Save it

	; ---- This is the restore
			cld

		; Set DI to start of rectangle on screen
			mov 		ax,  [DLine_Offset] ; Get line size
			mov		bx,  [MLastY]       ; Get Y into view
			mov		si,  bx 			; Save in SI
			add	     bx,  [ViewYLoc]     ; Add start of view
			mul		bx			   ; Find Y offset value. Place in AX

			mov		di,  [MLastX]     ; Find X offset value. Place in DI
			mov		bp,  di		   ; Save X in BP
			add		di,  [ViewXLoc]   ; Get start of view and add it
			shr		di,  1
			shr		di,  1

			add		di,  ax		     ; Add X and Y offsets
			add		di,  [Display_Page] ; Add in page offset


		; See if we need to clip Second XBlock
			mov		cx,		2			; Assume 2 XBlocks to write
			mov		dx,       [ScreenSizeX]	; Get screen size
			sub		dx,		4			; Get 2nd to last XBlock
			cmp		dx,		bp
			ja        @@Continue			; Both blocks on reload
			dec		cx					; Only one block on reload

	@@Continue:
		; Set all data from latches
			mov		ax,	GC_INDEX
			mov		ax,	00000h + BIT_MASK  ; Set all latch writes
			out		dx, 	ax

		; Insure Map Mask is set to all planes
			mov		ax,  0F02h	; 0F sets all planes. 02 is Map Mask
			mov		dx,	SC_INDEX
			out		dx,	ax

		; Set DX to number of lines to display
			mov		dx,	8		; Assume 8 scan lines
			mov		ax,	[ScreenSizeY] ; Get Screen size Y and put in ax
			sub		ax,	8		; Subtract to make mimimum clip

			cmp		ax,	si		; Does it need clipping? (SI = last Y)
			ja		@@ContinueC	; No, then go!

			sub		si,  ax		; Needs clipping!  Find new DX
			sub		dx,	si

	@@ContinueC:
		; Set BP to the distance from one scan line to next
			mov		bp,	[DLine_Offset]
			sub		bp,  cx    		; # XBlocks per scan line

		; Get the Screen Seg and put it in DS and ES
			mov		ax,  SCREEN_SEG
			mov		ds,  ax
			mov		es,	ax

		; Set SI to source
			mov		si,		MSAVEOFFSET

		; Save number to move (CX) in BX
			mov		bx,	cx

	   ; This section is for WRITES ------------------------------
	   ; The Reload will be clipped if appropriate

	 @@RowLoop:
		 ; Put 1 or 2 XBlocks
			rep movsb

		 ; Ready next iteration
			add		di,	bp	; Point DI to start of next line

		 ; Reload CX
			mov		cx,	bx

		 ; Are we done?
			dec		dx
			jnz		@@RowLoop	; No, then go and do another line


	   ; -- DONE!!

			pop		di si ds		; Restore regs
			pop		bp			; Return state
               ret

ENDP			_XMouse_InActive


; ------------------------ _XReload_TileW ---------------------------
; - This will "reload" a tile from the write page to display memory
; - storeage
; -
public		_XReload_TileW

PROC			_XReload_TileW

	ARG	STARTX:WORD, STARTY:WORD, XDELTA:WORD, YDELTA:WORD, OFFST:WORD

			push		bp
			mov		bp,	sp		; Save Stack frame

		; Save regs
			push		ds si di

		; Load DS.
			ASSUME	ds:  @fardata
			mov		ax,  @fardata
			mov		ds,  ax			; Set DS to fardata segment

	; -- Do the reload

		; Set SI to start of rectangle on screen
			mov 		ax,  [WLine_Offset] ; Get line size
			mul		[STARTY]			; Find Y offset

			mov		si,  [STARTX]     ; Find X offset value. Place in DI
			shr		si,  1
			shr		si,  1

			add		si,  ax		     ; Add X and Y offsets
			add		si,  [Write_Page]   ; Add in page offset

		; Set destination
			mov		di,	[OFFST]

		; Set all data from latches
			mov		dx,	GC_INDEX
			mov		ax,	00000h + BIT_MASK  ; Set all latch writes
			out		dx, 	ax

		; Insure Map Mask is set to all planes
			mov		ax,  0F02h	; 0F sets all planes. 02 is Map Mask
			mov		dx,	SC_INDEX
			out		dx,	ax

		; Set CX to the number of XBlocks to move per scan line
			mov       cx,  [XDELTA]
			shr		cx,  1
			shr		cx,  1

		; Set BX to the number of scan lines to do
			mov		bx,  [YDELTA]

		; Set AX to scan line dif.
			mov		ax,	[WLine_Offset]
			sub		ax,  cx			; Scan size minus tile size

		; Get the Screen Seg and put it in ES
			mov		dx,  SCREEN_SEG
			mov		es,	dx
			mov		ds,	dx

		; Save number of XBlocks (CX) in DX
			mov		dx,  cx

		; This section is for the MOVE ------------------------------
		@@Rowloop:

			rep  movsb

		; Adjust for next iteration
			add	si,	ax	; Point DI to start of next line

		; Re-load CX
			mov  cx,  dx

		; End of Rowloop.
			dec	  bx
			jnz    @@Rowloop


		; DONE!!!
			pop    di si ds

			pop	  bp			; Return state
			ret

ENDP	   _XReload_TileW


; ------------------------ _XSet_Clip ---------------------------
; - This will set the clip boundries
; -
public		_XSet_Clip

PROC			_XSet_Clip

	ARG	STARTX:WORD, STARTY:WORD, ENDX:WORD, ENDY:WORD

			push		bp
			mov		bp,	sp		; Save Stack frame

		; Save regs
			push		ds

		; Load DS.
			ASSUME	ds:  @fardata
			mov		ax,  @fardata
			mov		ds,  ax			; Set DS to fardata segment

	; -- Load the values
			mov		ax,		[STARTX]
			mov		[ClipSX],	ax
			mov		ax,		[STARTY]
			mov		[ClipSY],	ax
			mov		ax,		[ENDX]
			mov		[ClipEX],	ax
			mov		ax,		[ENDY]
			mov		[ClipEY],	ax

		; DONE!!!
			pop    ds

			pop	  bp			; Return state
			ret

ENDP	   _XSet_Clip


ENDS

END