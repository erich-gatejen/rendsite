; XTile  Assembler routine lib
;
; COPYRIGHT (C) 1993 Erich P Gatejen 1993  All Rights Reserved
;
; File: XTILE.ASM
;
; Mode X graphics manager.

; Ver 1.1 : Add sprites
;           Add 4-Pixel font   
;                 Add mouse interface           
;           Add put pixel

; Ver 2.0 : Major rewrite!
;                 Add virtual size
;                 Split-screen
;           More modes


IDEAL


INCLUDE "XTILE20!.USE"


DOSSEG

; LARGE memory model.
			MODEL   LARGE

; ------------------------------------------------------------------------
; ----------------------------- Data Seg ---------------------------------
; ------------------------------------------------------------------------
			FARDATA

; --- Mode set tables ----------------------------------------------------

; - Tables for each mode
XMODE_320x200:
	db      0e3h    ; dot clock
	db      02      ; # to change
	dw      00014h  ; turn off dword mode
	dw      0e317h  ; turn on byte mode
	dw      320     ; X
	dw      200     ; Y

XMODE_320x240:
	db      0e3h    ; dot clock
	db      10      ; # to change
	dw      00d06h  ; v-total
	dw      03e07h  ; overflow (bit 8 of vertical counts)
	dw      04109h  ; cell height
	dw      0ea10h  ; v-start
	dw      0ac11h  ; v-end ( and protect bits 0-7 )
	dw      0df12h  ; vertical displayed
	dw      00014h  ; turn off dword mode
	dw      0e715h  ; v-blank start
	dw      00616h  ; v-blank end
	dw      0e317h  ; turn on byte mode
	dw      320     ; X
	dw      240     ; Y

XMODE_360x200:
	db      0e7h    ; dot clock
	db      08      ; # to change
	dw      06b00h  ; h-total
	dw      05901h  ; horz displayed
	dw      05a02h  ; start horz blanking
	dw      08e03h  ; end horz blanking
	dw      05e04h  ; start h-sync
	dw      08a05h  ; end h-sync
	dw      00014h  ; turn off dword mode
	dw      0e317h  ; turn on byte mode
	dw      360     ; X
	dw      200     ; Y

XMODE_360x240:
	db      0e7h    ; dot clock
	db      17      ; # to change
	dw         06b00h        ; h-total
	dw         05901h        ; horz displayed
	dw         05a02h        ; start horz blanking
	dw         08e03h        ; end horz blanking
	dw         05e04h        ; start h-sync
	dw         08a05h        ; end h-sync
	dw      00d06h  ; v-total
	dw      03e07h  ; overflow of vertical counts
	dw      04109h  ; cell height
	dw      0ea10h  ; v-start
	dw      0ac11h  ; v-end ( and protect bits 0-7 )
	dw      0df12h  ; vertical displayed
	dw         02d13h        ; offset
	dw      00014h  ; turn off dword mode
	dw      0e715h  ; v-blank start
	dw      00616h  ; v-blank end
	dw      0e317h  ; turn on byte mode
	dw      360     ; X
	dw      240     ; Y

XMODE_376x282:
	db      067h    ; dot clock
	db      18      ; # to change
	dw         06e00h        ; h-total
	dw         05d01h        ; horz displayed
	dw         05e02h        ; start horz blanking
	dw         09103h        ; end horz blanking
	dw         06204h        ; start h-sync
	dw         08f05h        ; end h-sync
	dw         06206h        ; v-total
	dw         0f007h        ; overflow
	dw      06109h  ; cell height
	dw      0310fh  ; overflow
	dw         03710h        ; v-start
	dw         08911h        ; v-end
	dw         03312h        ; vertical displayed
	dw         02f13h        ; offset
	dw         00014h        ; turn off dword mode
	dw         03c15h        ; v-blank start
	dw         05c16h        ; v-blank end
	dw         0e317h        ; turn on byte mode
	dw      376     ; X
	dw      282      ; y


XMODE_320x400:
	db      0e3h    ; dot clock
	db      03      ; # to change
	dw         04009h        ; cell height
	dw      00014h  ; turn off dword mode
	dw      0e317h  ; turn on byte mode
	dw      320     ; X
	dw      400     ; Y

XMODE_320x480:
	db      0e3h    ; dot clock
	db      10      ; # to change
	dw      00d06h  ; v-total
	dw      03e07h  ; overflow
	dw      04009h  ; cell height
	dw      0ea10h  ; v-sync start
	dw      0ac11h  ; v-sync end
	dw      0df12h  ; vertical displayed
	dw      00014h  ; turn off dword mode
	dw      0e715h  ; v-blank start
	dw      00616h  ; v-blank end
	dw      0e317h  ; turn on byte mode
	dw      320     ; X
	dw      480     ; Y

XMODE_360x400:
	db      0e7h    ; dot clock
	db      09      ; # to change
	dw      06b00h  ; h-total
	dw      05901h  ; horz displayed
	dw      05a02h  ; start horz blanking
	dw      08e03h  ; end horz blanking
	dw      05e04h  ; start h sync
	dw      08a05h  ; end h sync
	dw         04009h        ; cell height
	dw      00014h  ; turn off dword mode
	dw      0e317h  ; turn on byte mode
	dw      360     ; X
	dw      400     ; Y


XMODE_360x480:
	db      0e7h    ; dot clock
	db      17      ; # to change
	dw         06b00h        ; h-total
	dw         05901h        ; horz displayed
	dw         05a02h        ; start horz blanking
	dw         08e03h        ; end horz blanking
	dw         05e04h        ; start h-sync
	dw         08a05h        ; end h-sync
	dw         00d06h  ; V-total
	dw         03e07h        ; overflow
	dw         04009h        ; cell height
	dw         0ea10h        ; v-sync start
	dw         0ac11h        ; v-sync end
	dw         0df12h        ; vertical displayed
	dw         02d13h        ; offset
	dw         00014h        ; turn off dword mode
	dw         0e715h        ; v-blank start
	dw         00616h        ; v-blank end
	dw         0e317h        ; turn on byte mode
	dw      360     ; X
	dw      480     ; Y

XMODE_360x360:
	db      0e7h    ; dot clock
	db      15      ; # to change
	dw         06b00h        ; h-total
	dw         05901h        ; horz displayed
	dw         05a02h  ; start horz blanking
	dw         08e03h        ; end horz blanking
	dw         05e04h        ; start h sync
	dw         08a05h        ; end h sync
	dw         04009h        ; cell height
	dw         08810h        ; v-sync start
	dw         08511h        ; v-sync end
	dw      06712h   ; vertical displayed
	dw      02d13h  ; offset
	dw         00014h        ; turn off dword mode
	dw         06d15h        ; v-blank start
	dw         0ba16h        ; v-blank end
	dw         0e317h        ; turn on byte mode
	dw      360     ; X
	dw      360     ; Y


XMODE_376x308:
	db      0e7h    ; dot clock
	db      18      ; # to change
	dw         06e00h        ; h-total
	dw         05d01h        ; horz displayed
	dw         05e02h        ; start horz blanking
	dw         09103h        ; end horz blanking
	dw         06204h        ; start h sync
	dw         08f05h        ; end h sync
	dw         06206h        ; v-total
	dw         00f07h        ; overflow
	dw      04009h  ; cell height
	dw      0310fh  ; overflow
	dw         03710h        ; v-sync start
	dw         08911h        ; v- sync end
	dw         03312h        ; vertical displayed
	dw         02f13h        ; offset
	dw         00014h        ; turn off dword mode
	dw         03c15h        ; v-blank start
	dw         05c16h        ; v-blank end
	dw         0e317h        ; turn on byte mode
	dw      376     ; X
	dw      308     ; Y

XMODE_376x564:
	db      0e7h    ; dot clock
	db      18      ; # to change
	dw         06e00h        ; h-total
	dw         05d01h        ; horz displayed
	dw         05e02h        ; start horz blanking
	dw         09103h        ; end horz blanking
	dw         06204h        ; start h sync
	dw         08f05h        ; end h sync
	dw         06206h        ; v-total
	dw         0f007h        ; overflow
	dw      06009h  ; v-blank start
	dw      0310fh  ; overflow
	dw         03710h        ; v-sync start
	dw         08911h        ; v-sync end
	dw         03312h        ; vertical displayed
	dw         02f13h        ; offset
	dw         00014h        ; turn off dword mode
	dw         03c15h        ; v-blank start
	dw         05c16h        ; v- blank end
	dw         0e317h        ; turn on byte mode
	dw      376
	dw      564


; - Table of available modes.  Position relates to mode number       D-SCAN
XMODE_TABLE:
	dw  OFFSET      XMODE_320x240           ; Mode 0.           1:1                       X
	dw  OFFSET      XMODE_320x200           ; Mode 1.                      X
	dw  OFFSET      XMODE_360x200           ; Mode 2.                      X
	dw  OFFSET      XMODE_360x240           ; Mode 3.                      X
	dw  OFFSET      XMODE_376x282           ; Mode 4.     1:1     DONT
	dw  OFFSET      XMODE_360x360           ; Mode 5.
	dw  OFFSET      XMODE_376x308           ; Mode 6.
	dw  OFFSET      XMODE_320x400           ; Mode 7.
	dw  OFFSET      XMODE_360x400           ; Mode 8.
	dw  OFFSET      XMODE_320x480           ; Mode 9.
	dw  OFFSET      XMODE_360x480           ; Mode 10.
	dw  OFFSET      XMODE_376x564           ; Mode 11.            DONT


; --- Other Tables --------------------------------------------------------
  ; Clip edge masks
	Left_Clip_Mask      DB  0FH, 0EH, 0CH, 08H
	Right_Clip_Mask     DB  01H, 03H, 07H, 0FH


; ---- Copyright notice --------------------------------------------------
Banner  db         'COPYRIGHT (C) 1993 Erich P Gatejen' ; Do not remove
	   db        ' All Rights Reserved '              ; Do not remove
ID         db      ' !!!!'


; ---- XTile local data -------------------------------------------------- 

; --- Define the current write page
Write_Page      dw      0       ; Offset into current write page
WLine_Offset    dw      ?       ; Line size in the current write page

; --- Define the Alt write page
Alt_Page        dw      0       ; Offset into the alternate write page
ALine_Offset    dw      ?       ; Line size in the alternate page


; --- Define the current display page
Display_Page    dw      ?       ; Offset into display page
DLine_Offset    dw      ?       ; Line size in current display page
DisplaySizeX    dw      ?       ; Size of display page (X)
DisplaySizeY    dw      ?       ; Size of display page (Y)
ViewXLoc        dw      ?       ; Starting X of view
ViewYLoc        dw      ?       ; Starting Y of view
MaxViewX        dw      ?       ; Maximum View X
MaxViewY        dw      ?       ; Max     View Y
ScreenSizeX     dw      ?       ; Actual screen size
ScreenSizeY     dw      ?       ; Actual screen size (Minus split screen)

; --- Split screen data
SplitY          dw      500     ; Split line.  If over screen size then no split
DScan           dw      ?       ; Double scan flag
OriginalY       dw      ?       ; The original Y screen size (Absolute size)

; --- Font data
; 8-pix font
UpLoaded8       dw      1       ; Is it uploaded?  Assume not.
Font_SiteU8     dw      ?       ; Offset of registered uploaded font
LABEL Font_Addr8        DWORD   ; For loading the address of an non-uploaded font
Font_SiteD8     dw      ?       ; Offset of registered not-uploaded font
FSS8            dw      ?       ; Segment.  Don't change
Char_Base8      dw      ?       ; Base character for font
Font_Mask_S8    dw      ?       ; Seg with masks for font set
Font_Mask_O8    dw      ?       ; Offset

; 4-pix font
UpLoaded4               dw      1       ; Is it uploaded?  Assume not.
Font_SiteU4     dw      ?       ; Offset of registered uploaded font
LABEL Font_Addr4        DWORD  ; For loading the address of an non-uploaded font
Font_SiteD4     dw      ?       ; Offset of registered not-uploaded font
FSS4            dw      ?       ; Segment.  Don't change
Char_Base4      dw      ?       ; Base character for font
Font_Mask_S4    dw      ?       ; Seg with masks for font set
Font_Mask_O4    dw      ?       ; Offset

; --- Mouse data
MousePage       dw      ?    ; Address of the page the mouse is working on
MouseLines      dw      ?    ; Number of lines in the mouses world
Mouse_Hndlr_O   dd  FAR ?    ; Address of the user defined mouse handler
LABEL MPointerMask DWORD     ; Address of the S:O for the next  two
MPointerMask_O  dw      ?    ; Location in user data of the mouse pntr mask
MPointerMask_S  dw      ?    ; Segment ^
MUEventMask     dw      ?    ; Events user wishes to be reported
MLastX          dw      ?    ; Last known X location of the mouse
MLastY          dw      ?    ; Last known Y location of the mouse
LockHandler     dw      0    ; Block re-entry into mouse handler
SplitOffset     dw      ?    ; Offset into display page in which starts the
			     ;  scan line that may be in the split page

; --- Clip Values
ClipSX          dw      ?       ; Clip starting X
ClipSY          dw      ?       ; Clip starting Y
ClipEX          dw      ?       ; Clip ending    X
ClipEY          dw      ?       ; Clip ending    Y

ENDS



; ------------------------------------------------------------------------
; ---------------------------- Code Seg ----------------------------------
; ------------------------------------------------------------------------
			SEGMENT         CODE       WORD PUBLIC  'CODE'


			ASSUME  cs:CODE

; --------------------------- _XInit_Mode -------------------------------
; - This will initialize Mode X.
; -
public    _XInit_Mode

PROC      _XInit_Mode   FAR

	ARG     Mode:WORD

	  push          bp
	  mov           bp,   sp    ; Save Stack frame
	  push         si di ds    ; Save calling frame

	  ASSUME   ds:  @fardata
	  mov    ax,  @fardata    ; Set DS to segment w/ Table
	  mov    ds,  ax

	  cld

    ; Get mode and decide if it is a double scan mode (one of first four )
	  mov    ax,     FALSE             ; FALSE = 0, assume no double scan
	  mov      cx,  [Mode]
	  cmp      cx,  3
	  jg       @@NotDoubleScan
	  mov            ax,  TRUE        ; It is double scan
   @@NotDoubleScan:
	  mov      [DScan],  ax    ; Set flag

    ; -- Set the mode

	  ; Let BIOS set mode 13h
	  mov            ax,     013h
	  int      010h

	  ; Disable Chain-4 and Synch reset
	  mov      dx,  SC_INDEX
	  mov      ax,  0604h
	  out      dx,  ax
	  mov      ax,  0100h
	  out      dx,  ax

	  ; Point to the mode table
	  mov      bx,  OFFSET XMODE_TABLE
	  shl      cx,  1                               ; Shift to word
	  add      bx,  cx                              ; Point to entry
	  mov      si,  [bx]                 ; Get address of specific mode table

	  ; Set the MISC register
	  lodsb                            ; Load dot clock
	  mov      dx,  MISC_OUTPUT
	  out      dx,  al                 ; Set the dot clock and H-Scanning

	  ; Undo the reset ( restart the sequencer )
	  mov      dx,  SC_INDEX
	  mov      ax,  0300h
	  out      dx,  ax

   ; -- Change the CRTC registers
	  mov      dx,  CRTC_INDEX

	  ; Unprotect the registers
	  mov      al,  011h
	  out      dx,  al
	  inc      dx
	  in       al,  dx
	  and      al,  07fh
	  out      dx,  al

	  ; Find length of CRTC table entries
	  dec      dx
	  xor      cx,cx
	  lodsb                  ; Get number
	  mov      cl,al         ; Put in lower byte of CX

   @@CRTCLoop:
	  lodsw                  ; Get output pair
	  out   dx,ax
	  loop  @@CRTCLoop

    ; -- Set local data

    ; - Set default page information

	  ; Use X size
	  lodsw                            ; Get X size
	  mov   [DisplaySizeX], ax
	  mov   [ScreenSizeX],  ax

	  shr   ax, 1
	  shr   ax, 1
	  mov   [WLine_Offset], ax  ; Set line size in XBlocks
	  mov   [DLine_Offset], ax  ; Set line size in XBlocks

	  ; Use Y size
	  lodsw                            ; Get Y size
	  mov   [DisplaySizeY], ax
	  mov   [ScreenSizeY],  ax
	  mov   [OriginalY],    ax

	  ; Zero the following
	  mov   ax,             0
	  mov   [Display_Page], ax  ; Display offset 0000
	  mov   [Write_Page], ax           ; Write page offset 0000
	  mov   [SplitY],       ax         ; No split screen
	  mov   [ViewX],        ax         ; View at base
	  mov   [ViewY],        ax         ; View at base
	  mov   [MaxViewX],     ax         ; No room to move
	  mov   [MaxViewY],     ax

	  ; For split screen, suppress split panning
	  mov   dx,          IN_STATUS0
	  in    al,             dx                 ; Toggle to indexing mode

	  mov   al,         010h+20h            ; Index10 (bit5 set to maintain display)
	  mov   dx,             AC_INDEXR            ; AC Index register
	  out   dx,             al
	  inc   dx                         ; Point to Data register (read only)
	  in    al,             dx             ; Get the current AC Mode Control
	  or    al,             20h            ; Enable suppress, bit 6
	  dec   dx                         ; Point to Data reg (for write)
	  out   dx,             al             ; Do it.

	  ; !!DONE!!
	  pop     ds di si             ; Return state
	  pop     bp
	  ret


ENDP                    _XInit_Mode


; --------------------------- _XSet_Write_Page ------------------------------
; - This will set a write page.  It will be the current write page for
; - most write operations ( the ones that don't use this page will say
; - so in thier discription )
; -
public          _XSet_Write_Page

PROC                    _XSet_Write_Page   FAR

	ARG     Offst:WORD, XSize:WORD

	push            bp
	mov             bp,     sp              ; Save Stack frame
	push            ds

	; Set the DS to local data
	ASSUME  ds:  @fardata
	mov     ax,  @fardata
	mov     ds,     ax

	; First, load the page offset
	mov     ax,           [Offst]   ; Get offset from pass
	mov     [Write_Page], ax        ; Put it in var

	; Calculate the line offset
	mov     ax,     [XSize]         ; Get the total size
	shr     ax, 1                   ; divided by 4 to make XBlocks
	shr     ax, 1
	mov     [WLine_Offset], ax      ; Set line size in XBlocks

     ; !!DONE!!
	pop     ds             ; Return state
	pop     bp
	ret

ENDP                    _XSet_Write_Page


; --------------------------- _XSet_Display_Page -------------------------------
; - This function set will set the display page.  The view will also be set
; -
public  _XSet_Display_Page

PROC    _XSet_Display_Page   FAR

	ARG   Offst:WORD, XSize:WORD, YSize:WORD, ViewX:WORD, ViewY:WORD


	push            bp
	mov             bp,     sp      ; Set up stack frame
	push            ds


	; Set DS to local data
	ASSUME  ds:  @fardata
	mov     ax,  @fardata
	mov     ds,  ax

	; Wait for retrace to end
	mov     dx, IN_STATUS1  ; Input Status #1 Register
   @@Wait:
	in      al, dx
	and     al, 08h
	jnz     @@Wait

   ; Set line offset.  CRTC Offset register
     ; Calculate the line offset
	mov     ax,             [XSize]  ; Get the total size
	mov     [DisplaySizeX], ax      ; Save it
	shr     ax, 1                   ; divide by 4 to make XBlocks
	shr     ax, 1
	mov     [DLine_Offset], ax      ; Save XBlocks per line
	shr        ax, 1                        ; divide by 2 to get register value
	
	; Set it
	mov        dx, CRTC_INDEX  ; Set port
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
	shr     cx, 1               ; Convert CX to XBlocks.  Figure pan later
	add     cx, ax          ; Find view upper left pixel
	add     cx, [Offst]     ; Add page offset
	; NOTE: This will leave any 0-3 pan  for later

	; Change CRTC
	mov        dx, CRTC_INDEX  ; Set port
	mov     al, 0Dh         ; Start display low.
	mov     ah, cl          ; Load low 8 bits
	out     dx, ax
	mov     al, 0Ch ; Start display high
	mov     ah, ch      ; Load high 8 Bits
	out     dx, ax

   ; Wait for a Vertical Retrace
	mov     dx, IN_STATUS1
   @@Wait2:
	in      al, dx
	and     al, 08h             ; Vertical Retrace Start?
	jz      @@Wait2         ; If Not, loop until it is

   ; Now Set the Horizontal Pixel Pan values
	mov     dx, 03C0h       ; The Attribute controller
	mov     al, 033h        ; Select Pixel Pan Register
	out  dx, al

     ; Get the pan value and send it
	mov     ax, [ViewX]     ; Get raw X View start
	and     al, 03          ; Peel off the significant bits
	shl     al, 1           ; Shift for 256 Color Mode
	out     dx, al          ; Send it

   ; Set some data values for this display page

	; Max View limits
	mov     ax, [XSize]             ; Get the page size
	sub      ax, [ScreenSizeX]      ; subtract the actual screen size
	dec     ax                      ; Adjust for count from 0
	mov     [MaxViewX], ax          ; Save

	mov     ax, [YSize]             ; Get the page size
	mov     [DisplaySizeY], ax      ; Save this
	sub     ax, [ScreenSizeY]       ; subtract the actual screen size
	dec     ax                      ; Adjust for count from 0
	mov     [MaxViewY], ax          ; Save

	; Save page offset
	mov     ax, [Offst]
	mov  [Display_Page], ax

	; Calculate Split offset
	mov     ax, [ViewY]             ; Get Y start
	add     ax, [SplitY]            ; Add split line down in view
	mul     [DLine_Offset]          ; Multiply times line size
	add     ax, [Display_Page]      ; Add display page start
	mov     [SplitOffset],  ax

   ; DONE!!
   @@Done:
	pop     ds             ; Return state
	pop     bp
	ret

ENDP                    _XSet_Display_Page

; --------------------------- _XSet_Display_PageP -------------------------------
; - This function set will set the display page.  The view will also be set
; - Anew palette will be loaded
public  _XSet_Display_PageP

PROC    _XSet_Display_PageP   FAR

	ARG   Offst:WORD, XSize:WORD, YSize:WORD, ViewX:WORD, ViewY:WORD, START:WORD, NUMBER:WORD, PAL_O:DWORD

	push            bp
	mov             bp,     sp      ; Set up stack frame
	push            si di ds


	; Set DS to local data
	ASSUME  ds:  @fardata
	mov     ax,  @fardata
	mov     ds,  ax

	; Wait for retrace to end
	mov     dx, IN_STATUS1  ; Input Status #1 Register
   @@Wait:
	in      al, dx
	and     al, 08h
	jnz     @@Wait

   ; Set line offset.  CRTC Offset register
     ; Calculate the line offset
	mov     ax,             [XSize]  ; Get the total size
	mov     [DisplaySizeX], ax      ; Save it
	shr     ax, 1                   ; divide by 4 to make XBlocks
	shr     ax, 1
	mov     [DLine_Offset], ax      ; Save XBlocks per line
	shr        ax, 1                        ; divide by 2 to get register value
	
	; Set it
	mov        dx, CRTC_INDEX  ; Set port
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
	shr     cx, 1               ; Convert CX to XBlocks.  Figure pan later
	add     cx, ax          ; Find view upper left pixel
	add     cx, [Offst]     ; Add page offset
	; NOTE: This will leave any 0-3 pan  for later

	; Change CRTC
	mov        dx, CRTC_INDEX  ; Set port
	mov     al, 0Dh         ; Start display low.
	mov     ah, cl          ; Load low 8 bits
	out     dx, ax
	mov     al, 0Ch ; Start display high
	mov     ah, ch      ; Load high 8 Bits
	out     dx, ax

   ; --- Set palette

	; Set CX to number of colors and BX to current
	mov     cx,     [NUMBER]
	mov	bx,	[START]

	; Set ds:si to pal data
	lds     si,     [PAL_O]

      ; -- Wait retrace
	mov     dx, IN_STATUS1
   @@WaitP:
	in      al, dx
	and     al, 08h     ; Vertical Retrace Start?
	jz      @@WaitP     ; If Not, loop until it is

     ; ------- Main loop
   @@Another:

	; Set color to change
	mov	dx,	PAL_SET_COLOR
	mov     al,	bl
	out	dx,     al

	; Set RGB
	mov	dx,     PAL_RGB_COLOR
	lodsb   			; Get byte
	out	dx,     al              ; Red
	lodsb
	out	dx,     al              ; Green
	lodsb
	out	dx,     al              ; Blue

	; Next values and loop if any left
	inc	bx
	dec	cx
	jnz	@@Another

   ; ---  Now Set the Horizontal Pixel Pan values
	; Set DS to local data
	ASSUME  ds:  @fardata
	mov     ax,  @fardata
	mov     ds,  ax


	mov     dx, 03C0h       ; The Attribute controller
	mov     al, 033h        ; Select Pixel Pan Register
	out  	dx, al

     ; Get the pan value and send it
	mov     ax, [ViewX]     ; Get raw X View start
	and     al, 03          ; Peel off the significant bits
	shl     al, 1           ; Shift for 256 Color Mode
	out     dx, al          ; Send it

   ; Set some data values for this display page

	; Max View limits
	mov     ax, [XSize]             ; Get the page size
	sub     ax, [ScreenSizeX]      ; subtract the actual screen size
	dec     ax                      ; Adjust for count from 0
	mov     [MaxViewX], ax          ; Save

	mov     ax, [YSize]             ; Get the page size
	mov     [DisplaySizeY], ax      ; Save this
	sub     ax, [ScreenSizeY]       ; subtract the actual screen size
	dec     ax                      ; Adjust for count from 0
	mov     [MaxViewY], ax          ; Save

	; Save page offset
	mov     ax, [Offst]
	mov  [Display_Page], ax

	; Calculate Split offset
	mov     ax, [ViewY]             ; Get Y start
	add     ax, [SplitY]            ; Add split line down in view
	mul     [DLine_Offset]          ; Multiply times line size
	add     ax, [Display_Page]      ; Add display page start
	mov     [SplitOffset],  ax

   ; DONE!!
   @@Done:
	pop     ds di si            ; Return state
	pop     bp
	ret

ENDP    _XSet_Display_PageP


; --------------------------- _XSet_AltPage --------------------------------
; - This function sets the alternate page
public  _XSet_AltPage

PROC    _XSet_AltPage   FAR

	ARG  XSize:WORD, Offst:WORD

	push    bp
	mov     bp,     sp      ; Set up stack frame
	push    ds


	; Set DS to local data
	ASSUME  ds:  @fardata
	mov     ax,  @fardata
	mov     ds,  ax


	; Set line size
	mov     ax,     [XSize]
	shr  ax,        1
	shr     ax,  1
	mov     [ALine_Offset], ax

	; Set page
	mov     ax,     [Offst]
	mov     [Alt_Page], ax

   ; DONE!!
   @@Done:
	pop     ds             ; Return state
	pop     bp
	ret

ENDP                    _XSet_AltPage


; --------------------------- _XSet_View ----------------------------------
; - This function set will set the view port within the current page
; - Return 0 if successful, else 1 (TRUE for C)
public  _XSet_View

PROC    _XSet_View   FAR

	ARG   ViewX:WORD, ViewY:WORD


	push            bp
	mov             bp,     sp      ; Set up stack frame
	push            ds


	; Set DS to local data
	ASSUME  ds:  @fardata
	mov     ax,  @fardata
	mov     ds,  ax

	; Wait for retrace to end
	mov     dx, IN_STATUS1  ; Input Status #1 Register
   @@Wait:
	in      al, dx
	and     al, 08h
	jnz     @@Wait           
 

   ; Set the Start Display Address to the new window
	mov     cx, [ViewX]      ; Get X start for the view
	cmp     cx, [MaxViewX]  ; Is it within range
	ja      @@Error         ; No, jump out.
	mov     [ViewXLoc], cx   ; Save the view location

	mov     ax, [ViewY]      ; Get Y start for the view
	cmp     ax, [MaxViewY]  ; Is it within range
	ja      @@Error         ; No, jump out.
	mov     [ViewYLoc], ax  ; Save the view loc

     ; Compute proper Display start address to use
	mul     [DLine_Offset]  ; AX = Y size (AX) * XBlocks per line
	shr     cx, 1           
	shr     cx, 1               ; Conver CX to XBlocks.  Figure pan later
	add     cx, ax             ; Find view upper left pixel
	add     cx, [Display_Page] ; Add page offset
	; NOTE: This will leave any 0-3 pan  for later

     ; Change CRTC
	mov        dx, CRTC_INDEX  ; Set port
	mov     al, 0Dh             ; Start display low.
	mov     ah, cl              ; Load low 8 bits
	out     dx, ax
	mov     al, 0Ch     ; Start display high
	mov     ah, ch          ; Load high 8 Bits
	out     dx, ax

   ; Wait for a Vertical Retrace
	mov     dx, IN_STATUS1
   @@Wait2:
	in      al, dx
	and     al, 08h         ; Vertical Retrace Start?
	jz      @@Wait2     ; If Not, loop until it is

      ; Now Set the Horizontal Pixel Pan values
	mov     dx, 03C0h       ; The Attribute controller
	mov     al, 033h        ; Select Pixel Pan Register
	out  dx, al

      ; Get the pan value and send it
	mov     ax, [ViewX]     ; Get raw X View start
	and     al, 03          ; Peel off the significant bits
	shl     al, 1           ; Shift for 256 Color Mode
	out     dx, al          ; Send it

      ; Calculate Split offset
	mov     ax, [ViewY]             ; Get Y start
	add     ax, [SplitY]            ; Add split line down in view
	mul     [DLine_Offset]          ; Multiply times line size
	add     ax, [Display_Page]      ; Add display page start
	mov     [SplitOffset],  ax

   ; DONE!!
	mov     ax,     0       ; No Error, Get outta here
	jmp     @@Done

   @@Error:
	mov     ax,     1       ; Error

   @@Done:
	pop     ds             ; Return state
	pop     bp
	ret

ENDP    _XSet_View


; --------------------------- _XWait_Retrace -------------------------------
; - Wait for vertical retrace
public  _XWait_Retrace

PROC    _XWait_Retrace   FAR

   ; Wait for a Vertical Retrace
	mov     dx, IN_STATUS1
   @@Wait1:
	in      al, dx
	and     al, 08h         ; Vertical Retrace Start?
	jz      @@Wait1     ; If Not, loop until it is

	ret

ENDP    _XWait_Retrace


; --------------------------- _XSet_SplitScreen ---------------------------
; - Set up the split screen.  !XTotal is slave to Display total!
; -
public  _XSet_SplitScreen

PROC    _XSet_SplitScreen   FAR

	ARG   YSIZE:WORD

	push            bp
	mov             bp,     sp                      ; Save Stack frame
	push            ds

    ; Set DS to local data
	ASSUME  ds:  @fardata
	mov     ax,  @fardata
	mov     ds,  ax


    ; Get the split line and save it
	mov     bx,       [OriginalY]
	mov     ax,       [YSIZE]
	sub     bx,       ax
	dec     bx                              ; Count from 0
	mov     [SplitY], bx                    ; Save it

    ; Adjust the screen size for the display page
	mov     bx,             [OriginalY]     ; Get the ACTUAL screen size
	sub     bx,             ax                      ; Subtract the split size
	mov     [ScreenSizeY], bx               ; Save as the new screen size
	   ; NOTE:  BX is now the starting line for the split

    ; Change the MAX Y view
	mov     cx, [DisplaySizeY]  ; Get the page size
	sub     cx, bx              ; subtract the actual screen size
	dec     cx                  ; Adjust for count from 0
	mov     [MaxViewY], cx      ; Save


    ; See if the double scan effects how we set the registers
	mov  cx, [DScan]
	shl  bx, cl             ; Shift up if so

      ; Wait for Vertical retrace to start
	mov     dx, IN_STATUS1
   @@Wait:
	in      al, dx
	and     al, 08h             ; Vertical Retrace Start?
	jz      @@Wait          ; If Not, loop until it is


      ; Set the registers
	cli                     ; Disallow interrupts
	mov  dx,  CRTC_INDEX
	mov  ah,  bl
	mov  al,  018h              ; Line compare register
	out  dx,  ax             ; Do lower 8 bits of line compare

	mov  ah,  bh
	and  ah,  1
	mov  cl,  4
	shl  ah,  cl            ; Bit 4 for over flow

	mov  al,  07h           ; Access overflow register
	out  dx,  al
	inc  dx
	in   al,  dx            ; Read the overflow register

	and  al,  not 10h       ; Or in our bit and send back
	or   al,  ah
	out  dx,  al
	dec  dx

	mov  ah,  bh
	and  ah,  2
				    ; optimize here!!!
	ror  ah,  1                 ; Second overflow bit to set
	ror  ah,  1
	ror  ah,  1

	mov  al,  09h       ; Bit 6 of the Max Scan line register
	out  dx,  al
	inc  dx
	in   al,  dx            ; Read it

	and  al,  not 40h       ; Or in our bit and send it
	or   al,  ah
	out  dx,  al

	sti                     ; Reallow interrupts

      ; Calculate Split offset
	mov     ax, [ViewYLoc]          ; Get Y start
	add     ax, [SplitY]            ; Add split line down in view
	mul     [DLine_Offset]          ; Multiply times line size
	add     ax, [Display_Page]      ; Add display page start
	mov     [SplitOffset],  ax

   ; - DONE!!
	pop     ds             ; Return state
	pop     bp
	ret

ENDP  _XSet_SplitScreen



; --------------------------- _XSet_Box --------------------------------
; - This function will draw a box to the passed value.  The X coord must
; - be MOD 4 aligned.
; -
public  _XSet_Box

PROC    _XSet_Box   FAR

	ARG   X_S:WORD, Y_S:WORD, X_D:WORD, Y_D:WORD, COLOR:BYTE

	push            bp
	mov             bp,     sp                      ; Save Stack frame
	push            ds di si

	; Set DS to local data
	ASSUME  ds:  @fardata
	mov     ax,  @fardata
	mov     ds,  ax

	cld

	; Set ES to display mem and DI to start of rectangle on screen
	mov     ax,  SCREEN_SEG
	mov     es,  ax

	mov     ax,  [WLine_Offset]  ; Get the line length for write page
	mov     bx,  ax              ; Save in BX for later             
	mul     [Y_S]                ; Find Y offset value. Place in AX

	mov     di,  [X_S]     ; Find X offset value. Place in DI
	shr     di,  1
	shr     di,  1         ; Adjust for planes

	add     di,  ax             ; Add X and Y offsets
	add     di,  [Write_Page]   ; Add in page offset

     ; Set all data from CPU and non from latches
	mov     dx,  GC_INDEX
	mov     ax,  0FF00h + BIT_MASK  ; Set all CPU writes
	out     dx,  ax

	; Insure Map Mask is set to all planes
	mov     ax,  0F02h      ; 0F sets all planes. 02 is Map Mask
	mov     dx,  SC_INDEX
	out     dx,  ax

     ; Insure X dist isn't 0 and put number of XBlocks in CX
	mov     cx,  [X_D]
	shr     cx,  1          ; Div by 4 so we have
	shr     cx,  1          ; the actual number addresses to fill
				; per line
	cmp     cx,  0
	jle     @@Done          ; Jump to stop if invalid


     ; SI will contain the number lines
	mov     si,     [Y_D]

     ; Load color into al
	mov     al,  [COLOR]

     ; Set BX to contain the distance from one scan line to next
	sub     bx,     cx    ; BX was already loaded with line length

   @@Rowloop:
	push    cx      ; Push cx so we can restore for each iteration

     ; Do the actual line.  REP STOSB.
	rep     stosb

     ; Adjust for next iteration
	pop     cx              ; Restore CX. ( [Pix/line]/4 )
	add     di,     bx      ; Point DI to start of next line

     ; End of Rowloop.  Dec SI, if it is NOT 0 then do next line
	dec     si
	jnz     @@Rowloop

     ; Else DONE!!

   @@Done:
	pop     si di ds
	pop     bp
	ret

ENDP                    _XSet_Box


; --------------------------- _XClear -------------------------------
; - This will clear all memory on the video card to the passed value
; -
public  _XClear

PROC    _XClear   FAR

	ARG     COLOR:WORD

	push    bp
	mov     bp,     sp                      ; Save Stack frame
	push    di

     ; Set MAP MASK to all planes
	mov     dx,     SC_INDEX                
	mov     ax,     0F02h
	out     dx,     ax

     ; Clear the screen
	mov     ax,  SCREEN_SEG
	mov     es,  ax
	sub     di,  di
	sub     ax,  ax
	mov     ax,  [COLOR]
	mov     ah,  al
	mov     cx,  8000h
	rep     stosw

     ; DONE!
	pop     di   ; Return state
	pop     bp
	ret

ENDP                    _XClear


; --------------------------- _XPut_Tile -------------------------------
; - This function will place a tile on the current write screen
; - It will be done a plane at a time and will expect the image data
; - to be laid out one plane at a time.
; - If there is an even number of XBlocks then a word write will be used
; -
public  _XPut_Tile

PROC    _XPut_Tile   FAR

	ARG  X_S:WORD,  Y_S:WORD, X_D:WORD, Y_D:WORD, IMAGE_OFF:DWORD

	push    bp
	mov     bp,     sp                      ; Save Stack frame
	push    ds di si

	cld

      ; Set all data from CPU and non from latches
	mov     dx,  GC_INDEX
	mov     ax,  0FF00h + BIT_MASK  ; Set all CPU writes
	out     dx,  ax

      ; Get the Screen Seg and put it in es and load ds to fardata
	mov     ax,  SCREEN_SEG
	mov     es,  ax

	ASSUME  ds:  @fardata
	mov     ax,  @fardata
	mov     ds,  ax          ; Set DS to local fardata

      ; Load CX with the number of XBlocks per image line
	mov     cx,    [X_D]
	shr     cx,    1
	shr     cx,    1         ; Divide by 4 to get
	mov     [X_D], cx        ; Save CX as new Delta

      ; Load di with destination offset and save it in DEST
	mov     ax,       [WLine_Offset]        ; Size of line in XBlocks
	mov     bx,     ax                              ; Save in DX
	mul     [Y_S]

	mov     di,     [X_S]
	shr     di,  1
	shr     di,     1

	add     di,     [Write_Page]
	add     di,     ax            ; Di is now loaded

	mov     [X_S],  di       ; Store it.  Steal X start

      ; Set [Y_S] to the distance from one scan line to next
			sub             bx,             cx                   ; minus number of XBlocks
			mov          [Y_S],    bx                       ; Move to store

		; Set BX to the number of scan lines
			mov             bx,     [Y_D]

		; Load ds:si with source
			lds             si,     [IMAGE_OFF]

		; Set up DX and AX for plane selection
			mov             al,  02h                ; Select Map Mask
			mov             dx,     SC_INDEX
			out             dx,     al
			inc             dx
			mov             al,     011h            ; Set for plane selection

		; Save CX in AH
			mov             ah,     cl

   ; This section is for the WRITES ------------------------------

   @@PlanesW:          ; Master plane loop.

	out     dx,  al        ; Set map mask
	mov     dx,  [Y_S]     ; Steal DX to speed up add

   @@RowloopW:          ; Scan line loop

      ; Do the actual line.  REP MOVSW.
	shr     cx,     1       ; Shift for word
	rep     movsw
	adc     cx,     0       ; Add 1(carry) to CX if dangling byte
	rep     movsb           ; Write dangle if there

      ; Adjust for next iteration
	mov     cl,  ah         ; Restore CX
	add     di,  dx         ; Point DI to start of next line

      ; !!End of Rowloop.  Dec BX, if it is NOT 0 then do next line
	dec     bx
	jnz     @@RowloopW

      ; Done with plane.  Reset Destination pointers.  Adjst plane
	mov     di,     [X_S]    ; Restore destination
	mov     bx,     [Y_D]    ; Restore scan line count

      ; !!End of Plane loop.
	mov     dx, (SC_INDEX+1) ; Restore DX for next plane
	shl     al, 1            ; Shift up a plane.
	jnc     @@PlanesW        ; If carry set, drop out

      ; !!!DONE
	pop             si di ds             ; Return state
	pop             bp
	ret

ENDP                    _XPut_Tile


; --------------------------- _XPut_TileA -------------------------------
; - This function will place a tile on the current write screen
; - It will be done a plane at a time and will expect the image data
; - to be laid out one plane at a time.
; - If there is an even number of XBlocks then a word write will be used
; -
; - This will work for any X alignment
public  _XPut_TileA

PROC    _XPut_TileA   FAR

	ARG  X_S:WORD,  Y_S:WORD, X_D:WORD, Y_D:WORD, IMAGE_OFF:DWORD

	push    bp
	mov     bp,     sp                      ; Save Stack frame
	push    ds di si

	cld

      ; Set all data from CPU and non from latches
	mov     dx,  GC_INDEX
	mov     ax,  0FF00h + BIT_MASK  ; Set all CPU writes
	out     dx,  ax

      ; Get the Screen Seg and put it in es and load ds to fardata
	mov     ax,  SCREEN_SEG
	mov     es,  ax

	ASSUME  ds:  @fardata
	mov     ax,  @fardata
	mov     ds,  ax          ; Set DS to local fardata

      ; Determine the start plane and save on stack
	mov     cx,  [X_S]
	and     cx,  3           ; Mask all but 2 LSB
	mov     al,  11h
	shl     al,  cl          ; Shift to first plane
	push    ax

      ; Load CX with the number of XBlocks per image line
	mov     cx,    [X_D]
	shr     cx,    1
	shr     cx,    1         ; Divide by 4 to get

      ; Load di with destination offset and save it in DEST
	mov     ax,    [WLine_Offset]   ; Size of line in XBlocks
	mov     bx,    ax               ; Save in BX
	mul     [Y_S]

	mov     di,    [X_S]
	shr     di,    1
	shr     di,    1

	add     di,    [Write_Page]
	add     di,    ax               ; Di is now loaded

	mov     [X_S], di               ; Store it.  Steal X start

      ; Set [Y_S] to the distance from one scan line to next
	sub     bx,    cx               ; minus number of XBlocks
	mov     [Y_S], bx               ; Move to store

      ; Set BX to the number of scan lines
	mov     bx,  [Y_D]

      ; Load ds:si with source
	lds     si,  [IMAGE_OFF]

      ; Set up DX and AX for plane selection
	mov     al,  02h        ; Select Map Mask
	mov     dx,  SC_INDEX
	out     dx,  al
	inc     dx
	pop     ax              ; Pop off the plane selection

      ; Save CX in AH
	mov     ah,     cl

	mov     [X_D],  4       ; Number of planes to do

   ; This section is for the WRITES ------------------------------

   @@PlanesW:          ; Master plane loop.

	out     dx,  al        ; Set map mask
	mov     dx,  [Y_S]     ; Steal DX to speed up add

   @@RowloopW:          ; Scan line loop

      ; Do the actual line.  REP MOVSW.
	shr     cx,     1       ; Shift for word
	rep     movsw
	adc     cx,     0       ; Add 1(carry) to CX if dangling byte
	rep     movsb           ; Write dangle if there

      ; Adjust for next iteration
	mov     cl,  ah         ; Restore CX
	add     di,  dx         ; Point DI to start of next line

      ; !!End of Rowloop.  Dec BX, if it is NOT 0 then do next line
	dec     bx
	jnz     @@RowloopW

      ; Done with plane.  Reset Destination pointers.  Adjst plane
	mov     di,     [X_S]    ; Restore destination
	mov     bx,     [Y_D]    ; Restore scan line count

      ; !!End of Plane loop.
	mov     dx, (SC_INDEX+1) ; Restore DX for next plane
	rol     al, 1            ; Shift up a plane.
	adc     di, 0            ; Adjust to next XBlock if neccessary
	mov     [X_S], di        ; Save in case it moved
	dec     [X_D]            ; Decremet number of planes to do
	jnz     @@PlanesW        ; If no planes left drop out

      ; !!!DONE
	pop             si di ds             ; Return state
	pop             bp
	ret

ENDP   _XPut_TileA



; --------------------------- _XTile_Size -----------------------------------
; - This function will return the total number of bytes an image requires
; - Div by 4 will yield the number of XBlocks
public          _XTile_Size

PROC                    _XTile_Size   FAR

	ARG      X_D:WORD,      Y_D:WORD


			push            bp
			mov             bp,     sp              ; Save Stack frame

		; Load AX with the total number of bytes
			mov             cx,     [X_D]
			mov             ax,     [Y_D]
			mul             cx                ; Yield is total bytes

		; AX holds return
			pop             bp
	       ret

ENDP                    _XTile_Size


; --------------------------- _XUpload_Tile -------------------------------
; - This function will place a tile in the display memory
; - It will be done a plane at a time and will expect the image data
; - to be laid out one plane at a time.
; - The tile will be laid out in a linear manner and even though it may be
; - sent to the current screen it will not be correctly displayed
; -
public          _XUpload_Tile
public          _XUpload_Sprite
LABEL          _XUpload_Sprite  FAR

PROC                    _XUpload_Tile   FAR

	ARG       DEST:WORD, ISIZE:WORD, IMAGE_OFF:DWORD

			push            bp
			mov             bp,     sp                      ; Save Stack frame
			push            ds di si

			cld

		; Get the Screen Seg and put it in es
			mov             ax,  SCREEN_SEG
			mov             es,  ax


		; Load di with destination offset and save it in DEST
			mov             di,             [DEST]
			mov       bx,           di       ; Store it

		; Load ds:si with source
			lds             si,     [IMAGE_OFF]

		; Get number of XBlocks per plane.
			mov             cx,  [ISIZE]
			shr             cx,  1
			shr             cx,  1
			mov             bp,  cx ; Save size in BP

		; Set all data from CPU and non from latches
			mov             dx,     GC_INDEX
			mov             ax,     0FF00h + BIT_MASK  ; Set all CPU writes
			out             dx,     ax

		; Set up DX and AX for plane selection
			mov             al,     02h             ; Select the MAP MASK
			mov             dx,     SC_INDEX
			out             dx,  al
			inc             dx
			mov             al,     11h             ; Plane 0

		; This section is for WRITES ------------------------------
	@@BeginWrite:

	@@PlanesW:                      ; Master plane loop.

			out          dx,        al      ; Set the plane

		; Do the actual line.  REP MOVSW.
			shr             cx,     1       ; Set for word writes
			rep             movsw
			adc             cx,     0       ; Add 1(carry) to CX to see if dangle
			rep       movsb ; Write the dangle if there.

		; Done with plane.  Reset Destination pointers.  Adjst plane
			mov             di,     bx              ; Restore Destination
			mov             cx,  bp         ; Restore CX.

		; !!End of Plane loop.
			shl             al,     1               ; Shift plane selector
			jnc             @@PlanesW               ; If no carry, then more to do


		; !!DONE!!
			pop             si di ds            ; Return state
			pop             bp
			ret

ENDP                    _XUpload_Tile


; --------------------------- _XPaste_Tile -------------------------------
; - This function will place a tile from the display memory to current screen
; -
public          _XPaste_Tile

PROC                    _XPaste_Tile   FAR


	ARG     X_S:WORD,       Y_S:WORD, X_D:WORD,     Y_D:WORD,       TILE:WORD

			push            bp
			mov             bp,     sp              ; Save Stack frame
			push            ds di si

	       cld

		; Load DS.

			ASSUME  ds:  @fardata
			mov             ax,  @fardata
			mov             ds,  ax                 ; Set DS to segment XTile data

		; Set DI to start of rectangle on screen
			mov             ax,  [WLine_Offset]
			mul             [Y_S]              ; Find Y offset value. Place in AX

			mov             di,  [X_S]        ; Find X offset value. Place in DI
			shr             di,  1
			shr             di,  1

			add             di,  ax            ; Add X and Y offsets
			add             di,  [Write_Page] ; Add in page offset

		; Set all data from latches
			mov             dx,     GC_INDEX
			mov             ax,     00000h + BIT_MASK  ; Set all latch writes
			out             dx,     ax

		; Insure Map Mask is set to all planes
			mov             ax,  0F02h      ; 0F sets all planes. 02 is Map Mask
			mov             dx,     SC_INDEX
			out             dx,     ax

		; Load CX with XBlocks per scan line
			mov             cx,  [X_D]
			shr             cx,  1          ; Div by 4 so we have
			shr             cx,  1         ; Xblock count

		; Set AX to the distance from one scan line to next
			mov             ax,             [WLine_Offset]
			sub             ax,             cx

		; Set SI to source
			mov             si,             [TILE]

		; Get the Screen Seg and put it in DS and ES
			mov             dx,  SCREEN_SEG
			mov             ds,  dx
			mov             es,     dx

		; Set DX to the number of scan lines
			mov             dx,     [Y_D]

		; Save the CX in BP
			mov             bp,             cx

		; This section is for WRITES ------------------------------
	@@Rowloop:

		; Do the actual line.  REP MOVSB.
			rep             movsb

		; Adjust for next iteration
			mov             cx,  bp ; Restore CX.
			add             di,     ax      ; Point DI to start of next line

		; End of Rowloop.  Dec DX, if it is NOT 0 then do next line
			dec             dx
			jnz             @@Rowloop

		; !!DONE!!
			pop             si di ds             ; Return state
			pop             bp
			ret

ENDP                    _XPaste_Tile;


;-------------------------- _XPaste_Sprite ------------------------------
; - This function will place a sprite from the display memory to current screen
; - It will NOT do masking
public          _XPaste_Sprite

PROC                    _XPaste_Sprite   FAR


	ARG     X_S:WORD,       Y_S:WORD, X_D:WORD,     Y_D:WORD,       TILE:WORD

			push            bp
			mov             bp,     sp              ; Save Stack frame
			push            ds di si

	       cld

		; Load DS.

			ASSUME  ds:  @fardata
			mov             ax,  @fardata
			mov             ds,  ax                 ; Set DS to segment XTile data

		; Set DI to start of rectangle on screen
			mov             ax,  [WLine_Offset]
			mul             [Y_S]              ; Find Y offset value. Place in AX

			mov             di,  [X_S]        ; Find X offset value. Place in DI
			shr             di,  1
			shr             di,  1          

			add             di,  ax            ; Add X and Y offsets
			add             di,  [Write_Page] ; Add in page offset

		; Set SI to source
			mov             si,             [TILE]

		; Find adjustment to source for alignment
			mov             ax,     [X_D]
			mov             cx,     [X_S]

			shr             ax,     1          ; Number of XBlocks a line
			shr             ax,     1
			mul             [Y_D]        ; Find size of each sprite alignment
			
			and             cx,     3          ; Mask all but 2 LSBs
			
			mul             cx                 ; Find new offset                            

			add             si,     ax         ; Adjust SI  

		; Get the Screen Seg and put it in DS and ES
			mov             ax,  SCREEN_SEG
			mov             ds,  ax
			mov             es,     ax

		; Set all data from latches
			mov             dx,     GC_INDEX
			mov             ax,     00000h + BIT_MASK  ; Set all latch writes
			out             dx,     ax

		; Insure Map Mask is set to all planes
			mov             ax,  0F02h      ; 0F sets all planes. 02 is Map Mask
			mov             dx,     SC_INDEX
			out             dx,     ax

		; Load CX with XBlocks per scan line
			mov             cx,  [X_D]
			shr             cx,  1          ; Div by 4 so we have
			shr             cx,  1         ; Xblock count

		; Set AX to the distance from one scan line to next
			mov             ax,             [WLine_Offset]
			sub             ax,             cx

		; Set DX to the number of scan lines
			mov             dx,     [Y_D]

		; Save CX in BP
			mov             bp,             cx

		; This section is for WRITES ------------------------------
	@@Rowloop:

		; Do the actual line.  REP MOVSB.
			rep             movsb

		; Adjust for next iteration
			mov             cx,  bp ; Restore CX.
			add             di,     ax      ; Point DI to start of next line

		; End of Rowloop.  Dec DX, if it is NOT 0 then do next line
			dec             dx
			jnz             @@Rowloop

		; !!DONE!!
			pop             si di ds             ; Return state
			pop             bp
			ret

ENDP                    _XPaste_Sprite


; --------------------------- _XPut_Tile_M -------------------------------
; - This function will place a tile on the current write screen
; - It will be done a plane at a time and will expect the image data
; - to be laid out one plane at a time.
; - All 0 pixels will leave the screen intact.
; -
public  _XPut_Tile_M

PROC    _XPut_Tile_M   FAR

	ARG     X_S:WORD,       Y_S:WORD, X_D:WORD,     Y_D:WORD,       IMAGE_OFF:DWORD

	push            bp
	mov             bp,     sp                      ; Save Stack frame
	push            ds di si

	cld

      ; Get the Screen Seg and put it in es
	mov             ax,  SCREEN_SEG
	mov             es,  ax


      ; Load CX with the number of XBlocks per scan line
	mov             cx,     [X_D]
	shr             cx,     1
	shr             cx,     1         ; Divide by 4 to get

      ; Load DS.
	ASSUME  ds:  @fardata
	mov             ax,  @fardata
	mov             ds,  ax                 ; Set DS to fardata segment

      ; Set [X_D] to the distance from one scan line to next
	mov             ax,             [WLine_Offset]
	sub             ax,             cx
	mov             [X_D],  ax

      ; Save number of XBlocks a line in upper cx
	mov             ch,             cl

		; Set BL to the number of scan lines and save
			mov             bx,     [Y_D]

		; Load di with destination offset and save it in DEST
			mov             ax,             [WLine_Offset]
			mul             [Y_S]

			mov             di,     [X_S]
			shr             di,  1
			shr             di,     1             ; Adjust to number of XBlocks

			add       di,   [Write_Page]
			add             di,     ax            ; Di is now loaded

			mov       [X_S],        di       ; Store it

		; Load ds:si with source
			lds             si,     [IMAGE_OFF]

		; Set all data from CPU and non from latches
			mov             dx,     GC_INDEX
			mov             ax,     0FF00h + BIT_MASK  ; Set all CPU writes
			out             dx,     ax

		; Set up DX and AX for plane selection
			mov             al,     02h     ; Plane 0 selected
			mov             dx,     SC_INDEX
			inc             dx
			mov             ax,     1111h   ; Set up AX

		; This section is for the WRITE ------------------------------
	@@PlanesB:          ; Master plane loop.

			out             dx,     al   ; Set the plane
			mov     dx,     [X_D]   ; Steal DX to speed up add

	@@RowloopB:             ; Scan line line

		; Do the actual line.
	@@PixelLoop:
			lodsb                           ; Get the pixel
			or              al,     al              ; Is it zero
			jz              @@NoDraw       ; If zero, don't draw it
			mov             [es:di],        al      ; Else, do draw it

	@@NoDraw:
			inc             di
			dec       cl
			jnz             @@PixelLoop

		; Adjust for next iteration
			mov             cl,  ch         ; Restore CL. ( [Pix/line]/4 )
			add             di,     dx      ; Point DI to start of next line

		; !!End of Rowloop.  Dec SI, if it is NOT 0 then do next line
			dec             bx
			jnz             @@RowloopB

		; Done with plane.  Reset Destination pointers.  Adjst plane
			mov             di,     [X_S]     ; Restore destination
			mov             bx,     [Y_D]   ; Restore scan line count
			shl             ah,     1               ; Adjust plane
			mov             al,  ah         ; Save the
			mov             dx,     SC_INDEX+1 ; Restore SC ptr
			jnc             @@PlanesB      ; If no carry, then more planes

		; !!!DONE
			pop             si di ds             ; Return state
			pop             bp
			ret

ENDP                    _XPut_Tile_M


; --------------------------- _XPut_TileA_M -------------------------------
; - This function will place a tile on the current write screen
; - It will be done a plane at a time and will expect the image data
; - to be laid out one plane at a time.
; - If there is an even number of XBlocks then a word write will be used
; -
; - This will work for any X alignment
public  _XPut_TileA_M

PROC    _XPut_TileA_M   FAR

	ARG  X_S:WORD,  Y_S:WORD, X_D:WORD, Y_D:WORD, IMAGE_OFF:DWORD

	push    bp
	mov     bp,     sp                      ; Save Stack frame
	push    ds di si

	cld

      ; Set all data from CPU and non from latches
	mov     dx,  GC_INDEX
	mov     ax,  0FF00h + BIT_MASK  ; Set all CPU writes
	out     dx,  ax

      ; Get the Screen Seg and put it in es and load ds to fardata
	mov     ax,  SCREEN_SEG
	mov     es,  ax

	ASSUME  ds:  @fardata
	mov     ax,  @fardata
	mov     ds,  ax          ; Set DS to local fardata

      ; Determine the start plane and save on stack
	mov     cx,  [X_S]
	and     cx,  3           ; Mask all but 2 LSB
	mov     ah,  11h
	shl     ah,  cl          ; Shift to first plane
	push    ax

      ; Load CX with the number of XBlocks per image line
	mov     cx,    [X_D]
	shr     cx,    1
	shr     cx,    1         ; Divide by 4 to get

      ; Load di with destination offset and save it in DEST
	mov     ax,    [WLine_Offset]   ; Size of line in XBlocks
	mov     bx,    ax               ; Save in BX
	mul     [Y_S]

	mov     di,    [X_S]
	shr     di,    1
	shr     di,    1

	add     di,    [Write_Page]
	add     di,    ax               ; Di is now loaded

	mov     [X_S], di               ; Store it.  Steal X start

      ; Set [Y_S] to the distance from one scan line to next
	sub     bx,    cx               ; minus number of XBlocks
	mov     [Y_S], bx               ; Move to store

      ; Set BX to the number of scan lines
	mov     bx,  [Y_D]

      ; Load ds:si with source
	lds     si,  [IMAGE_OFF]

      ; Set up DX and AX for plane selection
	mov     al,  02h        ; Select Map Mask
	mov     dx,  SC_INDEX
	out     dx,  al
	inc     dx
	pop     ax              ; Pop off the plane selection

      ; Save CL in CH
	mov     ch,     cl

	mov     [X_D],  4       ; Number of planes to do

   ; This section is for the WRITES ------------------------------

   @@PlanesW:          ; Master plane loop.

	mov     al,  ah        ; Prep ah
	out     dx,  al        ; Set map mask
	mov     dx,  [Y_S]     ; Steal DX to speed up add

   @@RowloopW:          ; Scan line loop

      ; Do the actual line.
   @@PixelLoop:
	lodsb
	or      al,      al    ; Is it zero
	jz      @@NoDraw       ; If zero, don't draw it
	mov     [es:di], al    ; Else, do draw it

   @@NoDraw:
	inc     di
	dec     cl
	jnz     @@RowloopW


      ; Adjust for next iteration
	mov     cl,  ch         ; Restore CX
	add     di,  dx         ; Point DI to start of next line

      ; !!End of Rowloop.  Dec BX, if it is NOT 0 then do next line
	dec     bx
	jnz     @@RowloopW

      ; Done with plane.  Reset Destination pointers.  Adjst plane
	mov     di,     [X_S]    ; Restore destination
	mov     bx,     [Y_D]    ; Restore scan line count

      ; !!End of Plane loop.
	mov     dx, (SC_INDEX+1) ; Restore DX for next plane
	rol     ah, 1            ; Shift up a plane.
	adc     di, 0            ; Adjust to next XBlock if neccessary
	mov     [X_S], di        ; Save in case it moved
	dec     [X_D]            ; Decremet number of planes to do
	jnz     @@PlanesW        ; If no planes left drop out

      ; !!!DONE
	pop             si di ds             ; Return state
	pop             bp
	ret

ENDP   _XPut_TileA_M



; --------------------------- _XMove_Tile ----------------------------------
; - This function will move a tile about on the current write page
; - Coord, Size, and destination must be givin
public          _XMove_Tile

PROC                    _XMove_Tile   FAR

  ARG  X_S:WORD, Y_S:WORD, X_E:WORD, Y_E:WORD, X_D:WORD, Y_D:WORD

	push            bp
	mov             bp,     sp              ; Save Stack frame
	push            ds si di

	cld

      ; Load DS.
	ASSUME  ds:  @fardata
	mov     ax,  @fardata
	mov     ds,  ax                 ; Set DS to fardata segment

	mov	cl,  2

      ; Load si with source X in XBlocks
	mov     si,  [X_S]
	shr     si,  cl

      ; Load di with destination X in XBlocks
	mov     di,  [X_D]
	shr     di,  cl

      ; Convert [X_E] to width in XBlocks
	mov     bx,  [X_E]
	shr     bx,  cl

	mov     ax,     bx   ; Save in AX
	mov     [X_E],  bx   ; Put back

      ; Determine the X direction
	cmp     si,     di
	jge     @@GoLeft

      ; Going right.  Copy right to left
	dec     ax
	add     si,     ax   ; Source starts at right edge
	add     di,     ax   ; Same with destination
	neg     bx           ; Make negative
	std                  ; Move backwards on string instructions

      ; Determine the Y direction
    @@GoLeft:
	mov     cx,  [WLine_Offset]
	mov     ax,  [Y_S]     	; Get the Y source start
	mov     dx,  [Y_D]      ; Get the Y destination
	cmp     ax,  dx         ; Going up or down
	jge     @@GoUp

      ; Rectangle going down.  Copy bottom to top
	mov     ax,  [Y_E]      ; Get the bottom coord
	add     dx,  ax         ; Add it to the destination, so
				;   it points to end of destination blk
	neg     cx              ; Make CX negative
	add     ax,     [Y_S]   ; Point AX to end of source blk
				;   by adding size to start

      ; Calcuate the offsets
    @@GoUp:
	push            dx              ; Save DX the torment of a multiply
	mul             [WLine_Offset]  ; Find Y adder for source
	add             si,     ax      ; Add it

	pop             ax              ; Pop off the destination Y into AX
	mul             [WLine_Offset]  ; Find Y adder for destination
	add             di,     ax      ; Add it

	sub             cx,     bx      ; Find the scan line data

      ; Add in the write page offset
	add             si,     [Write_Page]
	add             di,     [Write_Page]

      ; Get the Screen Seg and put it in DS and ES
	mov             ax,  SCREEN_SEG
	mov             ds,  ax
	mov             es,     ax

      ; Set all data from latches
	mov             dx,     GC_INDEX
	mov             ax,     00000h + BIT_MASK  ; Set all latch writes
	out             dx,     ax

      ; Insure Map Mask is set to all planes
	mov             ax,  0F02h      ; 0F sets all planes. 02 is Map Mask
	mov             dx,     SC_INDEX
	out             dx,     ax

      ; Set AX to the Scan line lenghth
	mov             ax,     [X_E]

      ; Set BX to the Y size
	mov             bx,     [Y_E]

      ; Set DX to the Scan line differential
	mov             dx,  cx

    ; Do the WRITES---------------------
    @@ScanLoop:

	mov             cx,  ax ; Set cx to the Scan size
	rep             movsb   ; Do the line

      ; Add the scan line differential to source and dest
	add             si,     dx
	add             di,     dx

      ; See if done with all lines
	dec             bx
	jnz             @@ScanLoop      ; No, then go and do another line

      ; !!DONE!!
	pop             di si ds
	pop             bp                      ; Return state
			ret

ENDP  _XMove_Tile


; --------------------------- _XMove_TileA --------------------------------
; - This function will move a tile to the alternate page
; - Coord, Size, and destination must be givin
public          _XMove_TileA

PROC                    _XMove_TileA   FAR

  ARG  X_S:WORD, Y_S:WORD, X_E:WORD, Y_E:WORD, X_D:WORD, Y_D:WORD

			push            bp
			mov             bp,     sp              ; Save Stack frame
			push            ds si di

			cld

		; Load DS.
			ASSUME  ds:  @fardata
			mov             ax,  @fardata
			mov             ds,  ax                 ; Set DS to fardata segment

		; Set all data from latches
			mov             dx,     GC_INDEX
			mov             ax,     00000h + BIT_MASK  ; Set all latch writes
			out             dx,     ax

		; Insure Map Mask is set to all planes
			mov             ax,  0F02h      ; 0F sets all planes. 02 is Map Mask
			mov             dx,     SC_INDEX
			out             dx,     ax

		; Set SI to start of rectangle in write page
			mov             ax,  [WLine_Offset] ; Get line size
			mov             cx,     ax                      ; Save in CX
			mul             [Y_S]                   ; Find Y offset

			mov             si,  [X_S]          ; Find X offset value. Place in DI
			shr             si,  1
			shr             si,  1

			add             si,  ax              ; Add X and Y offsets
			add             si,  [Write_Page]   ; Add in page offset

		; Set DI to start of rectangle in write page
			mov             ax,  [ALine_Offset] ; Get line size
			mov             bx,     ax                      ; Save in BX
			mul             [Y_D]                   ; Find Y offset

			mov             si,  [X_D]     ; Find X offset value. Place in DI
			shr             si,  1
			shr             si,  1

			add             si,  ax              ; Add X and Y offsets
			add             si,  [Alt_Page]     ; Add in page offset


		; Set AX to the Scan line lenghth in XBlocks
			mov             ax,     [X_E]
			shr             ax,  1
			shr             ax,     1

		; Make BX the scan line differntial for the alt page
			sub             bx,     ax

		; Make DX the scan line differntial for the write page
			sub             cx,     ax
			mov             dx,     cx

		; Set BP to the Y size
			mov             bp,  [Y_E]

		; Get the Screen Seg and put it in DS and ES
			mov             cx,  SCREEN_SEG
			mov             ds,  cx
			mov             es,     cx

	  ; Do the WRITES---------------------
	@@ScanLoop:

			mov             cx,  ax ; Set cx to the Scan size
			rep             movsb   ; Do the line

		; Add the scan line differential to source and dest
			add             si,     dx
			add             di,     bx

		; See if done with all lines
			dec             bp
			jnz             @@ScanLoop      ; No, then go and do another line

		; !!DONE!!
			pop             di si ds
			pop             bp                      ; Return state
			ret

ENDP                    _XMove_TileA


; --------------------------- _XRegister_Font8 ------------------------------
; - This function will register a 8-font.
; - The function will return in AX the actual number of bytes used.
; - The function expects each character to be laid out as for Upload_Tile,
; - one character at a time.  Each character is 8x by 10y.

public          _XRegister_Font8

PROC                    _XRegister_Font8   FAR

	ARG       DEST:WORD, NUMBER:WORD, BASE:WORD, UPLOAD:WORD, FONT_OFF:DWORD

			push            bp
			mov             bp,     sp                      ; Save Stack frame
			push            ds di si

			cld

		; Load DS.
			ASSUME  ds:  @fardata
			mov             ax,  @fardata
			mov             ds,  ax                 ; Set DS to fardata segment

		; Save the base char
			mov             ax,                     [BASE]
			mov             [Char_Base8],   ax

		; Will we upload this font?
			mov             cx,     [UPLOAD]
			mov             [UpLoaded8], cx ; Flag it       ( 0 is uploaded )
			jcxz            @@UploadFont            ; Yes!, then jump to it

		; Don't upload this font.  Just register and return.
			les             si,                       [FONT_OFF]
			mov             [Font_SiteD8],   si
			mov             ax,                       es
			mov             [Font_SiteD8+2], ax
			jmp       @@DONE

	@@UploadFont:
		; Check font size to insure no overrun
			mov             dx,     20
			mov             ax,     [NUMBER]
			mul             dx                      ; Mul number of chars by 20bytes
			add             ax,  [DEST]     ; Add destination to get proposed end
			cmp             ax,     STORE   ; Where is it in relation to begin
			ja              @@CONT          ; If above, no wrap of AX, OK
			mov             ax,     0               ; Else leave 0 in AX, indicating ERR
			jmp             @@DONE          ; And jump out

	@@CONT:
		; Save the size and location
			mov             ax,            [DEST]
			mov             [Font_SiteU8],  ax

		; Load di with destination offset and save it in DX
			mov             di,             ax          ; AX retains it from prev action
			mov       dx,       ax       ; Store it

		; Load ds:si with source
			lds             si,  [FONT_OFF]

		; Load es with SCREEN SEG
			mov             ax,  SCREEN_SEG
			mov             es,     ax

		; This section iterates for each char----------------------------
			mov             bx,     [NUMBER]        ; Set BL to number of characters

		; Set BP to destination, as was in DX
			mov             bp,  dx

		; Set all data from CPU and non from latches
			mov             dx,     GC_INDEX
			mov             ax,     0FF00h + BIT_MASK  ; Set all CPU writes
			out             dx,     ax

		; Set up DX plane selection
			mov             dx,     SC_INDEX

		; Set CX to number of scan lines in a char
			mov             cx,     10        ; Set CX to 10 scan lines

	@@BeginWrite:

		; Set AX up for plane selection
			mov             ax,     1102h   ; Plane 0 selected

		; This section loops through the planes for each char -----------
	@@PlanesW:                              ; Master plane loop.

			out          dx,        ax      ; Set the plane

		; Do the actual line.  REP MOVSW.  Move both XBlocks on each line
			rep             movsw

	  ; Done with plane.  Reset Destination pointers.  Adjst plane
			mov             di,     bp
			shl             ah,     1               ; Adjust plane. Carry set if done
			mov             cx,  10      ; Restore CX.

		; !!End of Plane loop.
			jnc             @@PlanesW      ; If no carry, not done with char

		; !!End of Char loop.

			add             bp,     20              ; Adjust destination for next char
			mov             di,     bp              ; Store it in DI

			dec             bx             ; Another char done
			jnz             @@BeginWrite   ; If not zero, not done with font set

		; !!DONE!!
		@@DONE:
			pop             si di ds            ; Return state
			pop             bp
			ret

ENDP                    _XRegister_Font8


; ----------------------------- _XChar8 ----------------------------------
; - This function will place a char from the current font to current screen
; - Uses the 8pix font
public          _XChar8

PROC                    _XChar8   FAR

	ARG     X_S:WORD,       Y_S:WORD, CHAR:WORD

			push            bp
			mov             bp,     sp              ; Save Stack frame
			push            ds di si

			cld

		; Load DS.
			ASSUME  ds:  @fardata
			mov             ax,  @fardata
			mov             ds,  ax                 ; Set DS to segment w/ Table

		; Set DI to start of character on screen
			mov             ax,  [WLine_Offset]
			mul             [Y_S]              ; Find Y offset value. Place in AX

			mov             di,  [X_S]        ; Find X offset value. Place in DI
			shr             di,  1
			shr             di,  1             ; Adjust to number of XBlocks

			add             di,  ax            ; Add X and Y offsets
			add             di,  [Write_Page] ; Add in page offset

		; Is the font uploaded or not
			mov             ax,     [UpLoaded8]
			cmp             ax,     UPLOADFONT
			je              @@FontIsUploaded        ; The font is uploaded

	  ; - Font is not uploaded
		; Set CX to the scan line differential
			mov             cx,  [WLine_Offset]
			sub             cx,  2                  ; Subtract the XBlocks of a char

		; Figure character offset and add to load ds:si font site
			mov             ax,     80                      ; Put font size in ax
			xor             bx,     bx                      ; Clear bx
			mov             bx,     [CHAR]         ; Put character place in BX
			sub             bx,     [Char_Base8]    ; Subtract base char
			mul             bx                              ; Mul size by place
			lds             si,  [Font_Addr8]
			add             si,  ax

		; Move the scan line differential from CX to BP
			mov             bp,  cx

		; Save the base destination in CX
			mov             cx,  di

		; Get the Screen Seg and put it in ES
			mov             ax,  SCREEN_SEG
			mov             es,     ax

		; Set all data from CPU and non from latches
			mov             dx,     GC_INDEX
			mov             ax,     0FF00h + BIT_MASK  ; Set all CPU writes
			out             dx,     ax

		; Set up DX and AX for plane selection
			mov             ax,     1102h   ; Plane 0 selected
			mov             dx,     SC_INDEX

		; This section is for WRITES ------------------------------

		; Set BH to number of lines to do
			mov       bx,  10               ; 10 scan lines per 8-pix font

	@@PlanesW:          ; Master plane loop.

			out             dx,     ax      ; Set the plane

	@@RowloopW:             ; Scan line loop

		; Do the actual line.  MOVSW.
			movsw

		; Adjust for next iteration
			add             di,     bp              ; Point DI to start of next line

		; !!End of Rowloop.  Dec BH, if it is NOT 0 then do next line
			dec             bx
			jnz             @@RowloopW

		; Done with plane.  Reset Destination pointers.  Adjst plane
			mov             di,     cx              ; Restore the destination start
			mov             bx,     10              ; Restore scan line count
			shl             ah,     1               ; Adjust plane

		; !!End of Plane loop.
			jnc             @@PlanesW               ; If no carry, then more to do.



		; Done with this
			jmp             @@DONE


	  ; - Font is uploaded
	  @@FontIsUploaded:
		; Calc the source and put in SI
			mov             si,     [Font_SiteU8]   ; Get base location
			mov             ax,     20                      ; Put font size in ax (in XBlock)
			mov             bx,     [CHAR]         ; Put character place in BX
			sub             bx,     [Char_Base8]    ; Subtract base char
			mul             bx                              ; Mul size by place
			add             si,     ax                      ; Put in SI

		; Insure Map Mask is set to all planes
			mov             ax,  0F02h      ; 0F sets all planes. 02 is Map Mask
			mov             dx,     SC_INDEX
			out             dx,     ax

		; Set all data from latches
			mov             dx,     GC_INDEX
			mov             ax,     00000h + BIT_MASK  ; Set all latch writes
			out             dx,     ax

		; Set AX to line dif.
			mov             ax,     [WLine_Offset]          ; Page width
			sub          ax,  2                   ; minus XBlocks per char

		; Get the Screen Seg and put it in DS and ES
			mov             dx,  SCREEN_SEG
			mov             ds,  dx
			mov             es,     dx

		; Set CX to the number of scan lines
			mov             cx,             10

		; This section is for WRITES ------------------------------
	@@Rowloop:

		; Do the actual line.  MOVSB.
					movsb
					movsb

		; Adjust for next iteration
			add             di,     ax      ; Point DI to start of next line

		; End of Rowloop.
			loop            @@Rowloop

		; !!DONE!!
	@@DONE:
			pop             si di ds             ; Return state
			pop             bp
			ret

ENDP                    _XChar8;


; ------------------------ _Register_Font_Masks8 ---------------------------
; - This function registers a font mask for the current 8pix font
; - This mask can be used for MFont character drawing
public          _Register_Font_Masks8

PROC                    _Register_Font_Masks8   FAR

	ARG     MASK_OFF:WORD, MASK_SEG:WORD  

			push            bp
			mov             bp,     sp              ; Save Stack frame
			push            ds

		; Load DS.
			ASSUME  ds:  @fardata
			mov             ax,  @fardata
			mov             ds,  ax                 ; Set DS to fardata segment 

		; Move location to local
			mov             ax,                     [MASK_OFF]
			mov       [Font_Mask_O8], ax

			mov             ax,                     [MASK_SEG]
			mov       [Font_Mask_S8], ax

		; !!DONE!!
			pop             ds
			pop             bp                      ; Return state
			ret

ENDP                    _Register_Font_Masks8


; ----------------------------- _XChar8_M ----------------------------------
; - This function will place a char from the current font to current screen
; - This function requires a font mask set to be registered.
; - Uses the 8pix font
public  _XChar8_M

PROC    _XChar8_M   FAR

	ARG     X_S:WORD,       Y_S:WORD, CHAR:WORD


	push    bp
	mov     bp,     sp                      ; Save Stack frame
	push    ds di si

	cld

      ; Load DS.
	ASSUME  ds:  @fardata
	mov     ax,  @fardata
	mov     ds,  ax                 ; Set DS to fardata segment

     ; Set DI to start of rectangle on screen
	mov     ax,   [WLine_Offset]
	mov     bx,   ax                ; Save in BX for later
	mul     [Y_S]                   ; Find Y offset value. Place in AX
	mov     di,   [X_S]             ; Find X offset value. Place in DI
	shr     di,   1
	shr     di,   1

	add     di,   ax                ; Add X and Y offsets
	add     di,   [Write_Page]      ; Add in page offset

      ; Is the font uploaded or not
	mov     cx,     [UpLoaded8]     ; UPLOADED is 0!
	jcxz    @@Uploaded              ; The font is uploaded

    ; - Font is not uploaded

      ; Figure character offset and add to load ds:si font site
	mov     ax,  80                 ; Put font size in ax
	mov     cx,  [CHAR]             ; Put character place in BX
	sub     cx,  [Char_Base8]       ; Subtract base char
	mul     cx                      ; Mul size by place
	lds     si,  [Font_Addr8]
	add     si,  ax

      ; Set BX to the line differential
	dec     bx                      ; BX was set above - first XBlock

      ; Save the base destination in BP
	mov     bp,  di

      ; Set all data from CPU and non from latches
	mov     dx,  GC_INDEX
	mov     ax,  0FF00h + BIT_MASK  ; Set all CPU writes
	out     dx,  ax

      ; Get the Screen Seg and put it in ES
	mov     ax,  SCREEN_SEG
	mov     es,     ax

      ; Set up DX and AX for plane selection
	mov     ax,     0102h   ; Plane 0 selected
	mov     dx,     SC_INDEX

    ; This section is for WORD WRITES ------------------------------

      ; Set CX to number of lines to do
	mov     cx,  10         ; 10 scan lines per 8-pix font

    @@PlanesW:                  ; Master plane loop.

	out     dx,  ax         ; Set the plane

    @@RowloopW:                 ; Scan line loop

      ; Do the actual DRAW.  MOV.
	lodsb                   ; Get the pixel
	or      al,       al    ; Is it zero
	jz      @@NoDraw1       ; If zero, don't draw it
	mov     [es:di],  al    ; Else, do draw it

    @@NoDraw1:
	inc     di

	lodsb                   ; Get the pixel
	or      al,       al    ; Is it zero
	jz      @@NoDraw2       ; If zero, don't draw it
	mov     [es:di],  al    ; Else, do draw it

    @@NoDraw2:
      ; Adjust for next iteration
	add     di,       bx    ; Point DI to start of next line

      ; !!End of Rowloop.  Dec CX, if it is NOT 0 then do next line
	loop    @@RowloopW

      ; Done with plane.  Reset Destination pointers.  Adjst plane
	mov     di,     bp      ; Restore the destination start
	mov     cx,     10      ; Restore scan line count

	mov     al,     02h     ; Restore MAP MASK pointer
	shl     ah,     1       ; Adjust plane

      ; !!End of Plane loop. If carry not set, then not done
	jnc     @@PlanesW

      ; Done with this
	jmp     @@DONE


    @@Uploaded:
    ; - Font is uploaded

      ; Set SI to mask offset
	mov     ax,     10              ; Put mask size in ax
	mov     cx,     [CHAR]          ; Put character place in CX
	sub     cx,     [Char_Base8]    ; Subtract base char
	mul     cx                      ; Mul size by place
	add     ax,     [Font_Mask_O8]  ; Find masks offset in mask set
	mov     si,     ax              ; Save it

      ; Calc the source and put in BX
	mov     bx,     [Font_SiteU8]   ; Get base location
	mov     ax,     20              ; Put font size in ax
	mov     cx,     [CHAR]          ; Put character place in CX
	sub     cx,     [Char_Base8]    ; Subtract base char
	mul     cx                      ; Mul size by place
	add     bx,     ax              ; Put in BX

      ; Set BP to the scan line differential
	mov     bp,     [WLine_Offset]
	dec     bp

      ; Set DS to mask area
	mov     ax,     [Font_Mask_S8]
	mov     ds,     ax

      ; Get the Screen Seg and put it in ES
	mov     ax,     SCREEN_SEG
	mov     es,     ax

      ; Set all data from latches
	mov     dx,     GC_INDEX
	mov     ax,     00000h + BIT_MASK  ; Set all latch writes
	out     dx,     ax

      ; Set up Map Mask
	mov     al,     02h     ; 02 is Map Mask
	mov     dx,     SC_INDEX
	out     dx,     al
	inc     dx

      ; Set CH to the number of scan lines
	mov     ch,     10

      ; Set CL to shift spaces
	mov     cl,     4

    ; This section is for WRITES ------------------------------
     @@Rowloop:
      ; This section prep the Map Mask
	lodsb                           ; Get mask byte

      ; Set mask for first write
	out     dx,     al              ; Set map mask

      ; Write the first byte
	mov     ah,             [es:bx]
	mov     [es:di],        ah

      ; Adjust di, bx
	inc     di
	inc     bx

      ; Set mask for second write
	shr     al,     cl        ; Move upper nibble down
	out     dx,     al        ; Set map mask

      ; Write the second byte
	mov     ah,             [es:bx]
	mov     [es:di],        ah

      ; Adjust bx
	inc     bx

      ; Adjust for next iteration
	add     di,     bp      ; Point DI to start of next line

      ; End of Rowloop.
	dec     ch
	jnz     @@Rowloop

      ; !!DONE!!
     @@DONE:
	pop     si di ds             ; Return state
	pop     bp
	ret

ENDP    _XChar8_M;

; ----------------------------- _XChar8_C ----------------------------------
; - This function will place a char from the current font to current screen
; - This function requires a font mask set to be registered.
; - Uses only the mask set to place one color characters
public  _XChar8_C

PROC    _XChar8_C   FAR

	ARG     X_S:WORD, Y_S:WORD, CHAR:WORD, COLOR:WORD


	push    bp
	mov     bp,     sp                      ; Save Stack frame
	push    ds di si

	cld

      ; Load DS.
	ASSUME  ds:  @fardata
	mov     ax,  @fardata
	mov     ds,  ax                 ; Set DS to fardata segment

     ; Set DI to start of rectangle on screen
	mov     ax,   [WLine_Offset]
	mov     bx,   ax                ; Save in BX for later
	mul     [Y_S]                   ; Find Y offset value. Place in AX
	mov     di,   [X_S]             ; Find X offset value. Place in DI
	shr     di,   1
	shr     di,   1

	add     di,   ax                ; Add X and Y offsets
	add     di,   [Write_Page]      ; Add in page offset

      ; Set SI to mask offset
	mov     ax,     10              ; Put mask size in ax
	mov     cx,     [CHAR]          ; Put character place in CX
	sub     cx,     [Char_Base8]    ; Subtract base char
	mul     cx                      ; Mul size by place
	add     ax,     [Font_Mask_O8]  ; Find masks offset in mask set
	mov     si,     ax              ; Save it

      ; Move the color into bx
	mov     bx,     [COLOR]

      ; Set BP to the scan line differential
	mov     bp,     [WLine_Offset]
	dec     bp

      ; Set DS to mask area
	mov     ax,     [Font_Mask_S8]
	mov     ds,     ax

      ; Get the Screen Seg and put it in ES
	mov     ax,     SCREEN_SEG
	mov     es,     ax

      ; Set all data from CPU and non from latches
	mov     dx,     GC_INDEX
	mov     ax,     0FF00h + BIT_MASK  ; Set all CPU writes
	out     dx,     ax

      ; Set up Map Mask
	mov     al,     02h     ; 02 is Map Mask
	mov     dx,     SC_INDEX
	out     dx,     al
	inc     dx

      ; Set CH to the number of scan lines
	mov     ch,     10

      ; Set CL to shift spaces
	mov     cl,     4

    ; This section is for WRITES ------------------------------
     @@Rowloop:
      ; This section prep the Map Mask
	lodsb                           ; Get mask byte

      ; Set mask for first write
	out     dx,       al            ; Set map mask

      ; Write the first set
	mov     [es:di],  bl

      ; Adjust di
	inc     di

      ; Set mask for second write
	shr     al,     cl        ; Move upper nibble down
	out     dx,     al        ; Set map mask

      ; Write the second set
	mov     [es:di],  bl

      ; Adjust for next iteration
	add     di,     bp      ; Point DI to start of next line

      ; End of Rowloop.
	dec     ch
	jnz     @@Rowloop

      ; !!DONE!!
     @@DONE:
	pop     si di ds             ; Return state
	pop     bp
	ret

ENDP    _XChar8_C;


;----------------------------- _XString8 ----------------------------------
; - This function will place a char from the current font to current screen
; - It will use the masked font on mask flag set to not 0.
public          _XString8

PROC                    _XString8   FAR

	ARG     X_S:WORD,       Y_S:WORD, MASKIT:WORD, STR_OFF:DWORD

			push            bp
			mov             bp,     sp              ; Save Stack frame
			push            ds di si

		; Load DS.
			ASSUME  ds:  @fardata
			mov             ax,  @fardata
			mov             ds,  ax                 ; Set DS to segment w/ Table

		; Move flag into CX
			mov             cx,  [MASKIT]

		; Load DS:SI
			lds             si,  [STR_OFF]

		; !!! This is the master string loop
	@@STRING:
			xor       ax,   ax
			mov             al,     [ds:si]         ; Get the char
			cmp             al,     0                       ; Is it the EOS
			je              @@DONE                  ; If so jump

		 ; Save cx
			push    cx

		 ; Build stack frame
			push            ax                              ; Push the char
			push            [Y_S]                   ; Push the Y coor
			push            [X_S]                   ; Push the X coor
		 ; To mask or not to mask
			jcxz            @@DontMask              ; If Flag = 0, dont mask
			call            _XChar8_M                       ; Put the masked char
			jmp             @@Continue

	@@DontMask:
			call      _XChar8                       ; Don't mask

	@@Continue:
			add             sp,     6                       ; Adjust the stack

			pop             cx                              ; Restore cx

			add             [X_S],  8               ; Move the cursor
			inc             si                              ; Point to next char
			jmp             @@STRING            ; Continue

		; !!DONE!!
	@@DONE:
			pop             si di ds             ; Return state
			pop             bp
			ret

ENDP                    _XString8


;----------------------------- _XString8_C ----------------------------------
; - This function will place a char from the current font to current screen
; - It will use the single color mask.
public  _XString8_C

PROC    _XString8_C   FAR

	ARG     X_S:WORD, Y_S:WORD, COLOR:WORD, STR_OFF:DWORD

	push    bp
	mov     bp,     sp              ; Save Stack frame
	push    ds di si

      ; Load DS.
	ASSUME  ds:  @fardata
	mov     ax,  @fardata
	mov     ds,  ax                 ; Set DS to segment w/ Table

      ; Load DS:SI
	lds             si,  [STR_OFF]

    ; !!! This is the master string loop
   @@STRING:
	xor     ax,     ax
	mov     al,     [ds:si]         ; Get the char
	cmp     al,     0                       ; Is it the EOS
	je      @@DONE                  ; If so jump

      ; Save cx
	push    cx

      ; Build stack frame
	push    [COLOR]                 ; Push the color
	push    ax                      ; Push the char
	push    [Y_S]                   ; Push the Y coor
	push    [X_S]                   ; Push the X coor

      ; Call the char routine
	call    _XChar8_C               ; Put the masked char

      ; Recover from call
	add     sp,     8               ; Adjust the stack

	pop     cx                      ; Restore cx

	add     [X_S],  8               ; Move the cursor
	inc     si                      ; Point to next char
	jmp     @@STRING                ; Continue

      ; !!DONE!!
    @@DONE:
	pop             si di ds             ; Return state
	pop             bp
	ret

ENDP                    _XString8_C


; --------------------------- _XRegister_Font4 ------------------------------
; - This function will register a 4-font.
; - The function will return in AX the actual number of bytes used.
; - The function expects each character to be laid out as for Upload_Tile,
; - one character at a time.  Each character is 4x by 6y.

public          _XRegister_Font4

PROC                    _XRegister_Font4   FAR

	ARG       DEST:WORD, NUMBER:WORD, BASE:WORD, UPLOAD:WORD, FONT_OFF:DWORD

			push            bp
			mov             bp,     sp                      ; Save Stack frame
			push            ds di si

			cld

		; Load DS.
			ASSUME  ds:  @fardata
			mov             ax,  @fardata
			mov             ds,  ax                 ; Set DS to fardata segment

		; Save the base char
			mov             ax,                     [BASE]
			mov             [Char_Base4],   ax

		; Will we upload this font?
			mov             cx,     [UPLOAD]
			mov             [UpLoaded4], cx ; Flag it       ( 0 is uploaded )
			jcxz            @@UploadFont            ; Yes!, then jump to it

		; Don't upload this font.  Just register and return.
			les             si,                       [FONT_OFF]
			mov             [Font_SiteD4],   si
			mov             ax,                       es
			mov             [Font_SiteD4+2], ax
			jmp       @@DONE

	@@UploadFont:
		; Check font size to insure no overrun
			mov             dx,     6
			mov             ax,     [NUMBER]
			mul             dx                      ; Mul number of chars by 20bytes
			add             ax,  [DEST]     ; Add destination to get proposed end
			cmp             ax,     STORE   ; Where is it in relation to begin
			ja              @@CONT          ; If above, no wrap of AX, OK
			mov             ax,     0               ; Else leave 0 in AX, indicating ERR
			jmp             @@DONE          ; And jump out

	@@CONT:
		; Save the size and location
			mov             ax,            [DEST]
			mov             [Font_SiteU4],  ax

		; Load di with destination offset and save it in DX
			mov             di,             ax          ; AX retains it from prev action
			mov       dx,           di       ; Store it

		; Load ds:si with source
			lds             si,  [FONT_OFF]

		; Load es with SCREEN SEG
			mov             ax,  SCREEN_SEG
			mov             es,     ax

		; This section iterates for each char----------------------------
			mov             bx,     [NUMBER]        ; Set BX to number of characters

		; Set BP to destination
			mov             bp,  dx        ; From above.

		; Set all data from CPU and non from latches
			mov             dx,     GC_INDEX
			mov             ax,     0FF00h + BIT_MASK  ; Set all CPU writes
			out             dx,     ax

		; Set up DX plane selection
			mov             dx,     SC_INDEX

	@@BeginWrite:

		; Set AX up for plane selection
			mov             ax,     1102h   ; Plane 0 selected

		; This section loops through the planes for each char -----------
	@@PlanesB:                              ; Master plane loop.

			out          dx,        ax      ; Set the plane

		; Do the actual write.  MOVSB.  Move 6 single XBlocks on each line
			movsb  ; 1
			movsb  ; 2
			movsb  ; 3
			movsb  ; 4
			movsb  ; 5
			movsb  ; 6

	@@EndPlane:
		; Done with plane.  Reset Destination pointers.  Adjst plane
			mov             di,     bp
			shl             ah,     1               ; Adjust plane. Carry set if done

		; !!End of Plane loop.
			jnc             @@PlanesB      ; If no carry, not done with char

		; !!End of Char loop.

			add             bp,     6               ; Adjust destination for next char
			mov             di,     bp              ; Store it in DI

			dec             bx             ; Another char done
			jnz             @@BeginWrite   ; If not zero, not done with font set

		; !!DONE!!
		@@DONE:
			pop             si di ds            ; Return state
			pop             bp
			ret

ENDP                    _XRegister_Font4


; ----------------------------- _XChar4 ----------------------------------
; - This function will place a char from the current font to current screen
; - Uses the 8pix font
public          _XChar4

PROC                    _XChar4   FAR

	ARG     X_S:WORD,       Y_S:WORD, CHAR:WORD

			push            bp
			mov             bp,     sp              ; Save Stack frame
			push            ds di si

			cld

		; Load DS.
			ASSUME  ds:  @fardata
			mov             ax,  @fardata
			mov             ds,  ax                 ; Set DS to segment w/ Table

		; Set DI to start of character on screen
			mov             ax,  [WLine_Offset]
			mul             [Y_S]              ; Find Y offset value. Place in AX

			mov             di,  [X_S]        ; Find X offset value. Place in DI
			shr             di,  1
			shr             di,  1             ; Adjust to number of XBlocks

			add             di,  ax            ; Add X and Y offsets
			add             di,  [Write_Page] ; Add in page offset

		; Is the font uploaded or not
			mov             ax,     [UpLoaded4]
			cmp             ax,     UPLOADFONT
			je              @@FontIsUploaded        ; The font is uploaded

	  ; - Font is not uploaded
		; Set CX to the scan line differential
			mov             cx,  [WLine_Offset]
			dec             cx                      ; Subtract the XBlock of a char

		; Figure character offset and add to load ds:si font site
			mov             ax,     24                      ; Put font size in ax
			mov             bx,     [CHAR]         ; Put character place in BX
			sub             bx,     [Char_Base4]    ; Subtract base char
			mul             bx                              ; Mul size by place
			lds             si,  [Font_Addr4]
			add             si,  ax

		; Save the base destination in BX
			mov             bx,  di

		; Get the Screen Seg and put it in ES
			mov             ax,  SCREEN_SEG
			mov             es,     ax

		; Set all data from CPU and non from latches
			mov             dx,     GC_INDEX
			mov             ax,     0FF00h + BIT_MASK  ; Set all CPU writes
			out             dx,     ax

		; Set up DX and AX for plane selection
			mov             ax,     1102h   ; Plane 0 selected
			mov             dx,     SC_INDEX

		; This section is for WRITES ------------------------------

	@@PlanesW:          ; Master plane loop.

			out             dx,     ax      ; Set the plane

	; Put a plane.  Completely un-rolled

		; Do line # 1
			movsb
			add             di,     cx              ; Point DI to start of next line

		; Do line # 2
			movsb
			add             di,     cx              ; Point DI to start of next line

		; Do line # 3
			movsb
			add             di,     cx              ; Point DI to start of next line

		; Do line # 4
			movsb
			add             di,     cx              ; Point DI to start of next line

		; Do line # 5
			movsb
			add             di,     cx              ; Point DI to start of next line

		; Do line # 6
			movsb


		; Done with plane.  Reset Destination pointers.  Adjst plane
			mov             di,     bx              ; Restore the destination start
			shl             ah,     1               ; Adjust plane

		; !!End of Plane loop.
			jnc             @@PlanesW               ; If no carry, then more to do.


		; Done with this
			jmp             @@DONE


	  ; - Font is uploaded
	  @@FontIsUploaded:
		; Calc the source and put in SI
			mov             si,     [Font_SiteU4]   ; Get base location
			mov             ax,     6                       ; Put font size in ax
			xor             bx,     bx                      ; Clear bx
			mov             bx,     [CHAR]         ; Put character place in BX
			sub             bx,     [Char_Base4]    ; Subtract base char
			mul             bx                              ; Mul size by place
			add             si,     ax                      ; Put in SI

		; Insure Map Mask is set to all planes
			mov             ax,  0F02h      ; 0F sets all planes. 02 is Map Mask
			mov             dx,     SC_INDEX
			out             dx,     ax

		; Set all data from latches
			mov             dx,     GC_INDEX
			mov             ax,     00000h + BIT_MASK  ; Set all latch writes
			out             dx,     ax

		; Set AX to line dif.
			mov             ax,     [WLine_Offset]          ; Page width
			dec          ax                     ; minus XBlocks per char

		; Get the Screen Seg and put it in DS and ES
			mov             dx,  SCREEN_SEG
			mov             ds,  dx
			mov             es,     dx

		; This section is for WRITES ------------------------------

	  ; - Completely un-rolled

		; Scan line #1
			movsb
			add             di,     ax      ; Point DI to start of next line

		; Scan line #2
			movsb
			add             di,     ax      ; Point DI to start of next line

		; Scan line #3
			movsb
			add             di,     ax      ; Point DI to start of next line

		; Scan line #4
			movsb
			add             di,     ax      ; Point DI to start of next line

		; Scan line #5
			movsb
			add             di,     ax      ; Point DI to start of next line

		; Scan line #6
			movsb


		; !!DONE!!
	@@DONE:
			pop             si di ds             ; Return state
			pop             bp
			ret

ENDP                    _XChar4;


; ------------------------ _Register_Font_Masks4 ---------------------------
; - This function registers a font mask for the current 4pix font
; - This mask can be used for MFont character drawing
public          _Register_Font_Masks4

PROC                    _Register_Font_Masks4   FAR

	ARG     MASK_OFF:WORD, MASK_SEG:WORD

			push            bp
			mov             bp,     sp              ; Save Stack frame
			push            ds

		; Load DS.
			ASSUME  ds:  @fardata
			mov             ax,  @fardata
			mov             ds,  ax                 ; Set DS to fardata segment

		; Move location to local
			mov             ax,                     [MASK_OFF]
			mov       [Font_Mask_O4], ax

			mov             ax,                     [MASK_SEG]
			mov       [Font_Mask_S4], ax

		; !!DONE!!
			pop             ds
			pop             bp                      ; Return state
			ret

ENDP                    _Register_Font_Masks4


; ----------------------------- _XChar4_M ----------------------------------
; - This function will place a char from the current font to current screen
; - This function requires a font mask set to be registered.
; - Uses the 4pix font
public   _XChar4_M

PROC     _XChar4_M   FAR

	ARG     X_S:WORD,       Y_S:WORD, CHAR:WORD

	push    bp
	mov     bp,     sp                      ; Save Stack frame
	push    ds di si

	cld

      ; Load DS.
	ASSUME  ds:  @fardata
	mov     ax,  @fardata
	mov     ds,  ax                 ; Set DS to fardata segment

      ; Set DI to start of rectangle on screen
	mov     ax,   [WLine_Offset]
	mov     bx,   ax                ; Save in BX as S/L differential
	mul     [Y_S]                   ; Find Y offset value. Place in AX

	mov     di,  [X_S]         ; Find X offset value. Place in DI
	shr     di,  1
	shr     di,  1

	add     di,  ax            ; Add X and Y offsets
	add     di,  [Write_Page]  ; Add in page offset

      ; Is the font uploaded or not
	mov     cx,  [UpLoaded4]   ; UPLOADED is 0!
	jcxz    @@Uploaded         ; The font is uploaded

    ; - Font is not uploaded

      ; Figure character offset and add to load ds:si font site
	mov     ax,  24            ; Put font size in ax
	mov     cx,  [CHAR]        ; Put character place in BX
	sub     cx,  [Char_Base4]  ; Subtract base char
	mul     cx                 ; Mul size by place
	lds     si,  [Font_Addr4]
	add     si,  ax

      ; Save the base destination in CX
	mov     cx,  di

      ; Get the Screen Seg and put it in ES
	mov     ax,  SCREEN_SEG
	mov     es,  ax

      ; Set all data from CPU and non from latches
	mov     dx,  GC_INDEX
	mov     ax,  0FF00h + BIT_MASK  ; Set all CPU writes
	out     dx,  ax

      ; Set up DX and AX for plane selection
	mov     ax,  0102h      ; Plane 0 selected
	mov     dx,  SC_INDEX

      ; This section is for WRITES ------------------------------

   @@PlanesW:          ; Master plane loop.

	out     dx,     ax      ; Set the plane


      ; Do the actual DRAW.  MOV.  Completely un-rolled!!

      ; Scan line #1
	lodsb                   ; Get the pixel
	or      al,       al    ; Is it zero
	jz      @@NoDraw1       ; If zero, don't draw it
	mov     [es:di],  al    ; Else, do draw it

      @@NoDraw1:
	add     di,     bx      ; Point DI to start of next line

      ; Scan line #2
	lodsb                   ; Get the pixel
	or      al,       al    ; Is it zero
	jz      @@NoDraw2       ; If zero, don't draw it
	mov     [es:di],  al    ; Else, do draw it

      @@NoDraw2:
	add     di,     bx      ; Point DI to start of next line

      ; Scan line #3
	lodsb                   ; Get the pixel
	or      al,       al    ; Is it zero
	jz      @@NoDraw3       ; If zero, don't draw it
	mov     [es:di],  al    ; Else, do draw it

      @@NoDraw3:
	add     di,     bx      ; Point DI to start of next line

      ; Scan line #4
	lodsb                   ; Get the pixel
	or      al,       al    ; Is it zero
	jz      @@NoDraw4       ; If zero, don't draw it
	mov     [es:di],  al    ; Else, do draw it

      @@NoDraw4:
	add     di,     bx      ; Point DI to start of next line

      ; Scan line #5
	lodsb                   ; Get the pixel
	or      al,       al    ; Is it zero
	jz      @@NoDraw5       ; If zero, don't draw it
	mov     [es:di],  al    ; Else, do draw it

      @@NoDraw5:
	add     di,     bx      ; Point DI to start of next line

      ; Scan line #6
	lodsb                   ; Get the pixel
	or      al,       al    ; Is it zero
	jz      @@NoDraw6       ; If zero, don't draw it
	mov     [es:di],  al    ; Else, do draw it

      @@NoDraw6:


      ; Done with plane.  Reset Destination pointers.  Adjst plane
	mov     di,  cx         ; Restore the destination start

	mov     al,  02h        ; Restore MAP MASK pointer
	shl     ah,  1          ; Adjust plane

      ; !!End of Plane loop. If carry not set, then not done
	jnc     @@PlanesW

      ; Done with this
	jmp     @@DONE


    @@Uploaded:
    ; - Font is uploaded

      ; Set SI to mask offset
	mov     ax,     3              ; Put mask size in ax
	mov     cx,     [CHAR]         ; Put character place in CX
	sub     cx,     [Char_Base4]   ; Subtract base char
	mul     cx                     ; Mul size by place
	add     ax,     [Font_Mask_O4] ; Find masks offset in mask set
	mov     si,     ax             ; Save it

      ; Calc the source and put in BX
	mov     bx,     [Font_SiteU4]  ; Get base location
	mov     ax,     6              ; Put font size in ax
	mov     cx,     [CHAR]         ; Put character place in BX
	sub     cx,     [Char_Base4]   ; Subtract base char
	mul     cx                     ; Mul size by place
	add     bx,     ax             ; Put in SI

      ; Set BP to the scan line differential
	mov     bp,     [WLine_Offset]

      ; Set DS to mask area
	mov     ax,     [Font_Mask_S4]
	mov     ds,     ax

      ; Get the Screen Seg and put it in ES
	mov     ax,  SCREEN_SEG
	mov     es,     ax

      ; Set all data from latches
	mov     dx,     GC_INDEX
	mov     ax,     00000h + BIT_MASK  ; Set all latch writes
	out     dx,     ax

      ; Set up Map Mask
	mov     al,     02h                ; 02 is Map Mask
	mov     dx,     SC_INDEX
	out     dx,     al
	inc     dx

      ; Set CL to shift spaces
	mov             cl,     4

    ; This section is for WRITES ------------------------------

      ; -- Completely unrolled

      ; Scan Line #1
      ; This section prep the Map Mask
	lodsb                      ; Get mask byte

      ; Set mask for first write
	out     dx,       al       ; Set map mask

      ; Write the byte
	mov     ah,       [es:bx]
	mov     [es:di],  ah

      ; Scan Line #2
      ; Set mask for next write
	shr     al,       cl       ; Move upper nibble down
	out     dx,       al       ; Set map mask

      ; Adjust for next scan line
	add     di,       bp       ; Point DI to start of next line
	inc     bx

      ; Write the second byte
	mov     al,       [es:bx]
	mov     [es:di],  al

      ; Adjust for next scan line
	add     di,       bp       ; Point DI to start of next line
	inc     bx

      ; Scan Line #3
      ; This section prep the Map Mask
	lodsb                      ; Get mask byte

      ; Set mask for first write
	out     dx,       al       ; Set map mask

      ; Write the byte
	mov     ah,       [es:bx]
	mov     [es:di],  ah

      ; Scan Line #4
      ; Set mask for next write
	shr     al,       cl       ; Move upper nibble down
	out     dx,       al       ; Set map mask

      ; Adjust for next scan line
	add     di,       bp       ; Point DI to start of next line
	inc     bx

      ; Write the second byte
	mov     al,       [es:bx]
	mov     [es:di],  al

      ; Adjust for next scan line
	add     di,       bp       ; Point DI to start of next line
	inc     bx

      ; Scan Line #5
      ; This section prep the Map Mask
	lodsb                      ; Get mask byte

      ; Set mask for first write
	out     dx,       al       ; Set map mask

      ; Write the byte
	mov     ah,       [es:bx]
	mov     [es:di],  ah

      ; Scan Line #6
      ; Set mask for next write
	shr     al,       cl       ; Move upper nibble down
	out     dx,       al       ; Set map mask

      ; Adjust for next scan line
	add     di,       bp       ; Point DI to start of next line
	inc     bx

      ; Write the second byte
	mov     al,       [es:bx]
	mov     [es:di],  al


      ; !!DONE!!
    @@DONE:
	pop     si di ds             ; Return state
	pop     bp
	ret

ENDP    _XChar4_M;


; ----------------------------- _XChar4_C ----------------------------------
; - This function will place a char from the current font to current screen
; - This function requires a font mask set to be registered.
; - Uses the 4pix font and the color specified.
public   _XChar4_C

PROC     _XChar4_C   FAR

	ARG     X_S:WORD,  Y_S:WORD, CHAR:WORD, COLOR:WORD

	push    bp
	mov     bp,     sp                      ; Save Stack frame
	push    ds di si

	cld

      ; Load DS.
	ASSUME  ds:  @fardata
	mov     ax,  @fardata
	mov     ds,  ax                 ; Set DS to fardata segment

      ; Set DI to start of rectangle on screen
	mov     ax,   [WLine_Offset]
	mov     bx,   ax                ; Save in BX as S/L differential
	mul     [Y_S]                   ; Find Y offset value. Place in AX

	mov     di,  [X_S]         ; Find X offset value. Place in DI
	shr     di,  1
	shr     di,  1

	add     di,  ax            ; Add X and Y offsets
	add     di,  [Write_Page]  ; Add in page offset

      ; Set SI to mask offset
	mov     ax,     3              ; Put mask size in ax
	mov     cx,     [CHAR]         ; Put character place in CX
	sub     cx,     [Char_Base4]   ; Subtract base char
	mul     cx                     ; Mul size by place
	add     ax,     [Font_Mask_O4] ; Find masks offset in mask set
	mov     si,     ax             ; Save it

      ; Set BX to color
	mov     bx,     [COLOR]

      ; Set BP to the scan line differential
	mov     bp,     [WLine_Offset]

      ; Set DS to mask area
	mov     ax,     [Font_Mask_S4]
	mov     ds,     ax

      ; Get the Screen Seg and put it in ES
	mov     ax,  SCREEN_SEG
	mov     es,     ax

      ; Set all data from latches
	mov     dx,     GC_INDEX
	mov     ax,     0FF00h + BIT_MASK  ; Set all latch writes
	out     dx,     ax

      ; Set up Map Mask
	mov     al,     02h                ; 02 is Map Mask
	mov     dx,     SC_INDEX
	out     dx,     al
	inc     dx

      ; Set CL to shift spaces
	mov     cl,     4

    ; This section is for WRITES ------------------------------

      ; -- Completely unrolled

      ; Scan Line #1
      ; This section prep the Map Mask
	lodsb                      ; Get mask byte

      ; Set mask for first write
	out     dx,       al       ; Set map mask

      ; Write the byte
	mov     [es:di],  bl

      ; Scan Line #2
      ; Set mask for next write
	shr     al,       cl       ; Move upper nibble down
	out     dx,       al       ; Set map mask

      ; Adjust for next scan line
	add     di,       bp       ; Point DI to start of next line

      ; Write the second byte
	mov     [es:di],  bl

      ; Adjust for next scan line
	add     di,       bp       ; Point DI to start of next line

      ; Scan Line #3
      ; This section prep the Map Mask
	lodsb                      ; Get mask byte

      ; Set mask for first write
	out     dx,       al       ; Set map mask

      ; Write the byte
	mov     [es:di],  bl

      ; Scan Line #4
      ; Set mask for next write
	shr     al,       cl       ; Move upper nibble down
	out     dx,       al       ; Set map mask

      ; Adjust for next scan line
	add     di,       bp       ; Point DI to start of next line

      ; Write the second byte
	mov     [es:di],  bl

      ; Adjust for next scan line
	add     di,       bp       ; Point DI to start of next line

      ; Scan Line #5
      ; This section prep the Map Mask
	lodsb                      ; Get mask byte

      ; Set mask for first write
	out     dx,       al       ; Set map mask

      ; Write the byte
	mov     [es:di],  bl

      ; Scan Line #6
      ; Set mask for next write
	shr     al,       cl       ; Move upper nibble down
	out     dx,       al       ; Set map mask

      ; Adjust for next scan line
	add     di,       bp       ; Point DI to start of next line

      ; Write the second byte
	mov     [es:di],  bl


      ; !!DONE!!
    @@DONE:
	pop     si di ds             ; Return state
	pop     bp
	ret

ENDP    _XChar4_C

;----------------------------- _XString4 ----------------------------------
; - This function will place a char from the current font to current screen
; - It will use the masked font on mask flag set to not 0.
public          _XString4

PROC                    _XString4   FAR

	ARG     X_S:WORD,       Y_S:WORD, MASKIT:WORD, STR_OFF:DWORD

			push            bp
			mov             bp,     sp              ; Save Stack frame
			push            ds di si

		; Load DS.
			ASSUME  ds:  @fardata
			mov             ax,  @fardata
			mov             ds,  ax                 ; Set DS to segment w/ Table

		; Move flag into CX
			mov             cx,  [MASKIT]

		; Load DS:SI
			lds             si,  [STR_OFF]

		; !!! This is the master string loop
	@@STRING:
			xor       ax,   ax
			mov             al,     [ds:si]         ; Get the char
			cmp             al,     0                       ; Is it the EOS
			je              @@DONE                  ; If so jump

		 ; Save cx
			push    cx

		 ; Build stack frame
			push            ax                              ; Push the char
			push            [Y_S]                   ; Push the Y coor
			push            [X_S]                   ; Push the X coor
		 ; To mask or not to mask
			jcxz            @@DontMask              ; If Flag = 0, dont mask
			call            _XChar4_M                       ; Put the masked char
			jmp             @@Continue

	@@DontMask:
			call      _XChar4                       ; Don't mask

	@@Continue:
			add             sp,     6                       ; Adjust the stack

			pop             cx                              ; Restore cx

			add             [X_S],  4               ; Move the cursor
			inc             si                              ; Point to next char
			jmp             @@STRING            ; Continue

		; !!DONE!!
	@@DONE:
			pop             si di ds             ; Return state
			pop             bp
			ret

ENDP                    _XString4


;----------------------------- _XString4_C ----------------------------------
; - This function will place a char from the current font to current screen
; - It will use the single color mask.
public  _XString4_C

PROC    _XString4_C   FAR

	ARG     X_S:WORD, Y_S:WORD, COLOR:WORD, STR_OFF:DWORD

	push    bp
	mov     bp,     sp              ; Save Stack frame
	push    ds di si

      ; Load DS.
	ASSUME  ds:  @fardata
	mov     ax,  @fardata
	mov     ds,  ax                 ; Set DS to segment w/ Table

      ; Load DS:SI
	lds             si,  [STR_OFF]

    ; !!! This is the master string loop
   @@STRING:
	xor     ax,     ax
	mov     al,     [ds:si]         ; Get the char
	cmp     al,     0                       ; Is it the EOS
	je      @@DONE                  ; If so jump

      ; Save cx
	push    cx

      ; Build stack frame
	push    [COLOR]                 ; Push the color
	push    ax                      ; Push the char
	push    [Y_S]                   ; Push the Y coor
	push    [X_S]                   ; Push the X coor

      ; Call the char routine
	call    _XChar4_C               ; Put the masked char

      ; Recover from call
	add     sp,     8               ; Adjust the stack

	pop     cx                      ; Restore cx

	add     [X_S],  4               ; Move the cursor
	inc     si                      ; Point to next char
	jmp     @@STRING                ; Continue

      ; !!DONE!!
    @@DONE:
	pop             si di ds             ; Return state
	pop             bp
	ret

ENDP                    _XString4_C



; --------------------------- _XPaste_Tile_M -------------------------------
; - This function will place a tile from the display memory to current screen
; - It will use the mask passed to it to leave background transparent
; - Max Y size is 255

public          _XPaste_Tile_M

PROC                    _XPaste_Tile_M   FAR

 ARG    X_S:WORD,       Y_S:WORD, X_D:WORD,     Y_D:WORD,       TILE:WORD, MASK_O:DWORD

			push            bp
			mov             bp,     sp                      ; Save Stack frame
			push            ds di si

	       cld
 
		; Load DS.
			ASSUME  ds:  @fardata
			mov             ax,  @fardata
			mov             ds,  ax                 ; Set DS to fardata segment

		; Set DI to start of rectangle on screen
			mov             ax,  [WLine_Offset]
			mul             [Y_S]              ; Find Y offset value. Place in AX

			mov             di,  [X_S]        ; Find X offset value. Place in DI
			shr             di,  1
			shr             di,  1             ; Adjust to number of XBlocks

			add             di,  ax            ; Add X and Y offsets
			add             di,  [Write_Page] ; Add in page offset

		; Put in source in BX
			mov             bx,     [TILE]

		; Get the Screen Seg and put it in ES
			mov             ax,  SCREEN_SEG
			mov             es,     ax

		; Set all data from latches
			mov             dx,     GC_INDEX
			mov             ax,     00000h + BIT_MASK  ; Set all latch writes
			out             dx,     ax

		; Set CX to the number of XBlocks per line
			mov             cx,     [X_D]
			shr             cx,     1
			shr             cx,     1

		; Get distance from end of one line to start of other and save
			mov             ax,             [WLine_Offset]  ; Get screen width
			sub             ax,             cx                      ; Del number of XBlocks
			mov             dx,       ax                    ; Save in DX

		; Save cl in ch.
			mov             ch,  cl ; Save in CH


		; Set DS:SI to mask area
			lds             si,     [MASK_O]

		; Set AH to the rows to do
			mov             ax,     [Y_D]   ; Puts number in AX
			mov             ah,     al              ; Puts number in ah

		; Move scan line differential from dx to bp
			mov             bp,  dx

		; Set up Map Mask
			mov             al,  02h        ; 02 is Map Mask
			mov             dx,     SC_INDEX
			out             dx,     al
			inc             dx

		; This section is for WRITES ------------------------------
	@@Rowloop:

	@@ScanLine:
		; This section prep the Map Mask
			lodsb                           ; Get mask byte

		; Set mask for write
			out             dx,     al              ; Set map mask

		; Write the XBlock
			mov             al,             [es:bx]
			mov             [es:di],        al

		; Adjust bx and si
			inc             bx
			inc             di

		; End of ScanLine
			dec             cl
			jnz             @@ScanLine

		; Adjust for next iteration
			add             di,     bp      ; Point DI to start of next line

		; Restore cl
			mov             cl,  ch

		; End of Rowloop.
			dec             ah
			jnz             @@Rowloop

		; !!DONE!!
	@@DONE:
			pop             si di ds             ; Return state
			pop             bp
			ret

ENDP                    _XPaste_Tile_M;


; --------------------------- _XPaste_Sprite_M ----------------------------
; - This function will place a sprite from the display memory to current screen
; - It will use the mask passed to it to leave background transparent
; - Max Y size is 255
public          _XPaste_Sprite_M

PROC                    _XPaste_Sprite_M   FAR

 ARG    X_S:WORD,       Y_S:WORD, X_D:WORD,     Y_D:WORD,       TILE:WORD, MASK_O:DWORD

			push            bp
			mov             bp,     sp                      ; Save Stack frame
			push            ds di si

	       cld
 
		; Load DS.
			ASSUME  ds:  @fardata
			mov             ax,  @fardata
			mov             ds,  ax                 ; Set DS to fardata segment

		; Set all data from latches
			mov             dx,     GC_INDEX
			mov             ax,     00000h + BIT_MASK  ; Set all latch writes
			out             dx,     ax

		; Set DI to start of rectangle on screen
			mov             ax,  [WLine_Offset] ; Scan line size
			mul             [Y_S]              ; Find Y offset value. Place in AX

			mov             di,  [X_S]        ; Find X offset value. Place in DI
			shr             di,  1
			shr             di,  1             ; Adjust to number of XBlocks

			add             di,  ax            ; Add X and Y offsets
			add             di,  [Write_Page] ; Add in page offset

		; Put source in BX
			mov             bx,     [TILE]

		; Find adjustment to source for alignment
			mov             ax,     [X_D]
			mov             cx,     [X_S]

			shr             ax,     1          ; Number of XBlocks a line
			shr             ax,     1
			mul             [Y_D]        ; Find size of each sprite alignment

			and             cx,     3          ; Mask all but 2 LSBs

			mul             cx                 ; Find new offset

			add             bx,     ax         ; Adjust BX

		; Save adjustment for mask for alignment in DX
			mov             dx,     ax         ; AX as calculated above

		; Set CX to the number of XBlocks
			mov             cx,     [X_D]
			shr             cx,     1
			shr             cx,     1

		; Get distance from end of one line to start of other and save
			mov             ax,     [WLine_Offset]  ; Get screen width
			sub             ax,     cx                      ; Del number of XBlocks

		; Set DS:SI to mask area
			lds             si,     [MASK_O]

		; Add alignment adjust to si
			add             si,  dx

		; Set AH to the rows to do
			mov             dx,     [Y_D]   ; Puts number in AX
			mov             bp,     ax              ; Move diff into BP
			mov             ah,     dl              ; Puts number in ah

		; Get the Screen Seg and put it in ES
			mov             dx,  SCREEN_SEG
			mov             es,     dx

		; Set up Map Mask
			mov             al,  02h        ; 02 is Map Mask
			mov             dx,     SC_INDEX
			out             dx,     al
			inc             dx

		; Save scan length (CL) in CH
			mov             ch,     cl

		; This section is for WRITES ------------------------------
	@@Rowloop:

	@@ScanLine:
		; This section prep the Map Mask
			lodsb                           ; Get mask byte

		; Set mask for write
			out             dx,     al              ; Set map mask

		; Write the XBlock
			mov             al,             [es:bx]
			mov             [es:di],        al

		; Adjust bx and si
			inc             bx
			inc             di

		; End of ScanLine
			dec             cl
			jnz             @@ScanLine


		; Adjust for next iteration
			add             di,     bp      ; Point DI to start of next line

		; Restore scan length
			mov             cl,  ch

		; End of Rowloop.
			dec             ah
			jnz             @@Rowloop

		; !!DONE!!
	@@DONE:
			pop             si di ds             ; Return state
			pop             bp
			ret

ENDP                    _XPaste_Sprite_M;


; ------------------------ _XSet_Pal_Color ---------------------------
; - This function uses the bios to set a single color in the palette
; - Does !not! wait for retrace
public _XSet_Pal_Color

PROC   _XSet_Pal_Color         FAR

   ARG     COLOR:WORD, RED:WORD, GREEN:WORD, BLUE:WORD

	push            bp
	mov             bp,     sp              ; Save Stack frame

	; Set color to change
	mov             ax,     [COLOR] 	; Load color number
	mov 		dx,     PAL_SET_COLOR
	out             dx,	al

	; Set red
	mov		dx,     PAL_RGB_COLOR
	mov             ax,     [RED]
	out             dx,	al

	; Set green
	mov             ax,     [GREEN]
	out             dx,	al

	; Set blue
	mov             ax,     [BLUE]
	out             dx,	al

	pop             bp                      ; Return state
	ret

ENDP  _XSet_Pal_Color

; ------------------------ _XSet_Pal_Block ---------------------------
; - This function uses the bios to set a block of colors in the palette
; - !Does! wait for retrace
public  _XSet_Pal_Block

PROC    _XSet_Pal_Block FAR

  ARG     START:WORD, NUMBER:WORD, PAL_O:DWORD

	push    bp
	mov     bp,     sp              ; Save Stack frame

	; Save registers
	push	ds si

	; Set CX to number of colors and BX to current
	mov     cx,     [NUMBER]
	mov	bx,	[START]

	; Set ds:si to pal data
	lds     si,     [PAL_O]

      ; -- Wait retrace
	mov     dx, IN_STATUS1
   @@WaitP:
	in      al, dx
	and     al, 08h     ; Vertical Retrace Start?
	jz      @@WaitP     ; If Not, loop until it is

   ; ------- Main loop
   @@Another:

	; Set color to change
	mov	dx,	PAL_SET_COLOR
	mov     al,	bl
	out	dx,     al

	; Set RGB
	mov	dx,     PAL_RGB_COLOR
	lodsb   			; Get byte
	out	dx,     al              ; Red
	lodsb
	out	dx,     al              ; Green
	lodsb
	out	dx,     al              ; Blue

	; Next values and loop if any left
	inc	bx
	dec	cx
	jnz	@@Another

   ; --- Done
	pop     si ds
	pop     bp                      ; Return state
	ret

ENDP                    _XSet_Pal_Block


; ------------------------ _XPut_Pixel ------------------------------------
; - Put a pixel to the screen.
; -
public  _XPut_Pixel

PROC    _XPut_Pixel  FAR

	ARG     X_LOC:WORD, Y_LOC:WORD, COLOR:WORD

			push            bp
			mov             bp,     sp              ; Save Stack frame

			push            ds di


		; Load DS.
			ASSUME  ds:  @fardata
			mov             ax,  @fardata
			mov             ds,  ax                 ; Set DS to fardata segment

		; Set DI to the XBlock
			mov             ax,  [WLine_Offset]  ; Scan line size
			mul             [Y_LOC]            ; Find Y offset value. Place in AX

			mov             di,  [X_LOC]      ; Find X offset value. Place in DI
			mov       cx,   di                 ; For later to determine the mask
			shr             di,  1
			shr             di,  1             ; Adjust to number of XBlocks

			add             di,  ax            ; Add X and Y offsets
			add             di,  [Write_Page] ; Add in page offset


		; Set ah to the mask
			and             cx,     03h             ; Get the lower 2 bits
			mov             ax,     0102h     ; MAP MASK
			shl             ah,     cl              ; Shift to app. plane

		; Set up Map Mask
			mov             dx,     SC_INDEX
			out             dx,     ax

		; Set all data from CPU and non from latches
			mov             dx,     GC_INDEX
			mov             ax,     0FF00h + BIT_MASK  ; Set all CPU writes
			out             dx,     ax

		; Get the Screen Seg and put it in DS
			mov             ax,  SCREEN_SEG
			mov             ds,     ax

		; Get the color
			mov             ax,     [COLOR] 

		; Put the Pixel
			mov             [di],   al

		; !!DONE!!
			pop             di ds             ; Return state
			pop             bp
			ret

ENDP            _XPut_Pixel


; ------------------------ _XPut_Line ------------------------------------
; - Put a line to the screen.
; -
; - Thanks to Matt Pritchard for some of this code
public  _XPut_Line
PROC    _XPut_Line      FAR

	ARG  XSTART:WORD, YSTART:WORD, XEND:WORD, YEND:WORD, COLOR:WORD


	push    bp
	mov     bp,     sp              ; Save Stack frame
	push    ds di si

      ; Load DS.
	ASSUME  ds:  @fardata
	mov     ax,  @fardata
	mov     ds,  ax                 ; Set DS to fardata segment

	cld

      ; Set all data from CPU
	mov     dx,     GC_INDEX
	mov     ax,     0FF00h + BIT_MASK
	out     dx,     ax

      ; Set up ES
	mov     ax,     SCREEN_SEG
	mov     es,     ax

      ; Set up Map Mask
	mov     al,  02h        ; 02 is Map Mask
	mov     dx,  SC_INDEX
	out     dx,  al
	inc     dx

      ; Check Line Type

	mov     si,  [XSTART]
	mov     di,  [XEND]
	cmp     si,  di         ; Is XSTART < XEND?
	je      @@Vertical      ; If X1=X2, it is a vertical line
	jl      @@NoSwap        ; If X1 < X2, no swap is needed

	xchg    si,  di         ; Swap so lower is in SI

    @@NoSwap:

	mov     ax,  [YSTART]   ;
	cmp     ax,  [YEND]     ; YSTART = YEND?
	je      @@Horizontal    ; If so, it is a horizontal line

	jmp     @@Brezhem       ; Otherwise, use Brezhem's algorithm

    ; Draw a horizontal line
    @@Horizontal:
	mul     [WLine_Offset]  ; Get Y offset into write page
	mov     dx,     ax      ; Save in DX

	mov     ax,     si
	and     si,     3       ; Mask out plane bits

	mov     bl, [Left_Clip_Mask+si] ; Get Left Edge Mask
	mov     cx,     di              ; Get Right edge, Save XSTART
	and     di,     3               ; Mask out scan
	mov     bh, [Right_Clip_Mask+di] ; Get Right Edge Mask byte

      ; Convert AX and CX to XBlocks
	shr     ax,     1
	shr     ax,     1
	shr     cx,     1
	shr     cx,     1

      ; Find start address
	mov     di,     [Write_Page]
	add     di,     dx              ; Point to start of scan line
	add     di,     ax              ; Point to start pixel

	sub     cx,     ax              ; Set CX to number of XBlocks - 1
	jnz     @@Long                  ; Jump if more that one XBlock

	and     bl,     bh              ; Otherwise, merge clip masks

    @@Long:
	mov     dx,     SC_INDEX+1      ; Point to SC data

      ; Set left mask
	mov     al,     bl
	out     dx,     al

      ; Get Color
	mov     ax,     [COLOR]
	mov     bl,     al      ; Save color in bl

      ; Set first XBlock
	stosb

	jcxz    @@DoneThis      ; Done if only one XBlock

      ; Dec cx and see if any middle XBlocks
	dec     cx
	jz      @@NoMiddleX

     ; Draw middle XBlocks

      ; Set map mask to all planes
	mov     al,     0Fh
	out     dx,     al

      ; Restore color and draw middle segments
	mov     al,     bl
	rep     stosb

    @@NoMiddleX:
      ; Do Left clip
	mov     al,     bh
	out     dx,     al
	stosb

    @@DoneThis:
	jmp     @@Done

    ; Draw vertical line
    @@Vertical:
	mov     ax,     [YSTART]
	mov     si,     [YEND]
	cmp     ax,     si         ; Is Y1 < Y2?
	jle     @@NoSwap2          ; if so, Don't Swap

	xchg    ax,     si         ; Ok, swap lower into ax

    @@NoSwap2:
	sub     si,     ax
	inc     si

      ; Get start address
	mul     [WLine_Offset]
	mov     dx,     di      ; Save XSTART in dx
	shr     di,     1       ; Convert to XBlocks
	shr     di,     1

	add     di,     ax
	add     di,     [Write_Page]

      ; Select Plane
	mov     cl,     dl      ; Get lower of XSTART
	and     cl,     3       ; Mask of plane selectoin bits
	mov     ax,     0102h   ; Map Mask register access
	shl     ah,     cl      ; Shift to plane
	mov     dx,     SC_INDEX
	out     dx,     ax

      ; Setup for writes
	mov     ax,      [COLOR]         ; Get color
	mov     bx,      [WLine_Offset]  ; Scan line differential

    @@VertLoop:
	mov     [es:di], al              ; Write a pixel
	add     di     , bx              ; Next scan line
	dec     si
	jz      @@EndLoop                ; Done?

	mov     [es:di], al              ; Write a pixel
	add     di     , bx              ; Next scan line
	dec     si
	jnz     @@VertLoop               ; More?

    @@EndLoop:
	jmp     @@Done

    ; Brezhem's algorithm
    @@Brezhem:

      ; Load write page into di
	mov     di, [Write_Page]

      ; Load starting values
	mov     ax, [YSTART]
	mov     bx, [YEND]
	mov     cx, [XSTART]

	cmp     bx, ax          ; Is Y2 >= Y1 is?
	jnc     @@YOK           ; If yes, OK!

	xchg    bx, ax          ; Otherwise swap
	mov     cx, [XEND]      ; Get New Starting X

    @@YOK:
      ; Find start address
	mul     [WLine_Offset]
	add     di, ax
	mov     ax, cx
	shr     ax, 1   ; Convert to XBlocks
	shr     ax, 1
	add     di, ax

      ; Push color and start mask in same word
	mov     ax,     [COLOR]
	xchg    ah,     al
	mov     al,     11h
	and     cl,     3
	shl     al,     cl      ; Find mask
	push    ax

      ; Prep map mask
	xchg    ah,     al
	mov     al,     02h     ; Map Mask register
	mov     dx,     SC_INDEX
	out     dx,     ax

      ; Load position values
	mov     ax,     [XSTART]
	mov     bx,     [YSTART]
	mov     cx,     [XEND]
	mov     dx,     [YEND]

      ; Load line size in BP
	mov     bp,     [WLine_Offset]

      ; Check Y delta
	sub     dx,     bx          ; Find delta
	jnc     @@YDeltaOK          ; Jump if Y2 >= Y1, delta ok

	add     bx,     dx          ; Otherwise fixup
	neg     dx
	xchg    ax,     cx

    @@YDeltaOK:
	mov     bx, 08000H          ; Seed for fraction AC
	sub     cx, ax              ; figure Delta_X
	jc      @@DrawLeft          ; if negative, go left

	jmp     @@DrawRight         ; Draw Line that slopes right

    @@DrawLeft:
	neg     cx                  ; abs(Delta_X)
	cmp     cx, dx              ; is Delta_X < Delta_Y?
	jb      @@SteepLeft         ; yes, so go do steep line
				    ; (Delta_Y iterations)

    ; Draw a Shallow line to the left
    @@ShallowLeft:
	xor     ax, ax              ; zero low word of Delta_Y * 10000h
	sub     ax, dx              ; DX:AX <- DX * 0FFFFh
	sbb     dx, 0               ; include carry
	div     cx                  ; divide by Delta_X

	mov     si, bx              ; SI = Accumulator
	mov     bx, ax              ; BX = Add fraction
	pop     ax                  ; Get Color, Bit mask
	mov     dx, SC_INDEX+1      ; Sequence controller data register
	inc     cx                  ; Inc Delta_X so we can unroll loop

      ; Loop (x2) to Draw Pixels, Move Left, and Maybe Down...
    @@SLLLoop:
	mov     [es:di], ah
	dec     cx
	jz      @@SLLExit           ; Delta_X--, Exit if done

	add     si, bx              ; add numerator to accumulator
	jnc     @@SLLL2nc           ; move down on carry

	add     di, bp              ; Move Down one line...

    @@SLLL2nc:
	dec     di                  ; Left one addr
	ror     al, 1               ; Move Left one plane, back on 0 1 2
	cmp     al, 87h             ; wrap?, if AL <88 then Carry set
	adc     di, 0               ; Adjust Address: DI = DI + Carry
	out     dx, al              ; Set up New Bit Plane mask

	mov     [es:di], al         ; set pixel
	dec     cx
	jz      @@SLLExit           ; Delta_X--, Exit if done

	add     si, bx              ; add numerator to accumulator,
	jnc     @@SLLL3nc           ; move down on carry

	add     di, bp              ; Move Down one line...

    @@SLLL3nc:                      ; Now move left a pixel...
	dec     di                  ; Left one addr
	ror     al, 1               ; Move Left one plane, back on 0 1 2
	cmp     al, 87h             ; Wrap?, if AL <88 then Carry set
	adc     di, 0               ; Adjust Address: DI = DI + Carry
	out     dx, al              ; Set up New Bit Plane mask
	jmp     @@SLLLoop           ; loop until done

    @@SLLExit:
	jmp     @@Done              ; Done

    ; Draw a steep line to the left
    @@SteepLeft:
	xor     ax, ax              ; zero low word of Delta_Y * 10000h
	xchg    dx, cx              ; Delta_Y switched with Delta_X
	div     cx                  ; divide by Delta_Y

	mov     si, bx              ; SI = Accumulator
	mov     bx, ax              ; BX = Add Fraction
	pop     ax                  ; Get Color, Bit mask
	mov     dx, SC_INDEX+1      ; Sequence controller data register
	inc     cx                  ; Inc Delta_Y so we can unroll loop
 
      ; Loop (x2) to Draw Pixels, Move Down, and Maybe left
    @@STLLoop:

	mov     [es:di], ah         ; set first pixel
	dec     cx
	jz      @@STLExit           ; Delta_Y--, Exit if done

	add     si, bx              ; add numerator to accumulator
	jnc     @@STLnc2            ; No carry, just move down!
 
	dec     di                  ; Move Left one addr
	ror     al, 1               ; Move Left one plane, back on 0 1 2
	cmp     al, 87h             ; Wrap?, if AL <88 then Carry set
	adc     di, 0               ; Adjust Address: DI = DI + Carry
	out     dx, al              ; Set up New Bit Plane mask

    @@STLnc2:
	add     di, bp              ; advance to next line.

	mov     [es:di], ah         ; set pixel
	dec     cx
	jz      @@STLExit           ; Delta_Y--, Exit if done

	add     si, bx              ; add numerator to accumulator
	jnc     @@STLnc3            ; No carry, just move down!
 
	dec     di                  ; Move Left one addr
	ror     al, 1               ; Move Left one plane, back on 0 1 2
	cmp     al, 87h             ; Wrap?, if AL <88 then Carry set
	adc     di, 0               ; Adjust Address: DI = DI + Carry
	out     dx, al              ; Set up New Bit Plane mask
 
    @@STLnc3:
	add     di, bp              ; advance to next line.
	jmp     @@STLLoop           ; Loop until done

    @@STLExit:
	jmp     @@Done              ; Done!

    ; Draw a line that goes to the Right...
    @@DrawRight:
	cmp     cx, dx              ; is Delta_X < Delta_Y?
	jb      @@SteepRight        ; yes, so go do steep line
				    ; (Delta_Y iterations)

    ; Draw a Shallow line to the Right in Mode X
    @@ShallowRight:
	xor     ax, ax              ; zero low word of Delta_Y * 10000h
	sub     ax, dx              ; DX:AX <- DX * 0FFFFh
	sbb     dx, 0               ; include carry
	div     cx                  ; divide by Delta_X

	mov     si, bx              ; SI = Accumulator
	mov     bx, ax              ; BX = Add Fraction
	pop     ax                  ; Get Color, Bit mask
	mov     dx, SC_INDEX+1      ; Sequence controller data register
	inc     cx                  ; Inc Delta_X so we can unroll loop

      ; Loop (x2) to Draw Pixels, Move Right, and Maybe Down...
    @@SLRLoop:
	mov     [es:di], ah         ; set first pixel, mask is set up
	dec     cx
	jz      @@SLRExit           ; Delta_X--, Exit if done..

	add     si, bx              ; add numerator to accumulator
	jnc     @@SLR2nc            ; don't move down if carry not set

	add     di, bp              ; Move Down one line...

    @@SLR2nc:                       ; Now move right a pixel...
	rol     al, 1               ; Move Right one addr if Plane = 0
	cmp     al, 12h             ; Wrap? if AL >12 then Carry not set
	adc     di, 0               ; Adjust Address: DI = DI + Carry
	out     dx, al              ; Set up New Bit Plane mask

	mov     [es:di], ah         ; set pixel
	dec     cx
	jz      @@SLRExit           ; Delta_X--, Exit if done..

	add     si, bx              ; add numerator to accumulator
	jnc     @@SLR3nc            ; don't move down if carry not set

	add     di, bp              ; Move Down one line...

    @@SLR3nc:
	rol     al, 1               ; Move Right one addr if Plane = 0
	cmp     al, 12h             ; Wrap? if AL >12 then Carry not set
	adc     di, 0               ; Adjust Address: DI = DI + Carry
	out     dx, al              ; Set up New Bit Plane mask
	jmp     @@SLRLoop           ; loop till done

    @@SLRExit:
	jmp     @@Done

    ; Draw a Steep line to the Right in Mode X

    @@SteepRight:
	xor     ax, ax              ; zero low word of Delta_Y * 10000h
	xchg    dx, cx              ; Delta_Y switched with Delta_X
	div     cx                  ; divide by Delta_Y

	mov     si, bx              ; SI = Accumulator
	mov     bx, ax              ; BX = Add Fraction
	pop     ax                  ; Get Color, Bit mask
	mov     dx, SC_INDEX+1      ; Sequence controller data register
	inc     cx                  ; Inc Delta_Y so we can unroll loop

    ; Loop (x2) to Draw Pixels, Move Down, and Maybe Right

    @@STRLoop:
	mov     [es:di], ah         ; set first pixel, mask is set up
	dec     cx
	jz      @@Done              ; Delta_Y--, Exit if Done

	add     si, bx              ; add numerator to accumulator
	jnc     @@STRnc2            ; if no carry then just go down...

	rol     al, 1               ; Move Right one addr if Plane = 0
	cmp     al, 12h             ; Wrap? if AL >12 then Carry not set
	adc     di, 0               ; Adjust Address: DI = DI + Carry
	out     dx, al              ; Set up New Bit Plane mask

    @@STRnc2:
	add     di, bp              ; advance to next line.

	mov     [es:di], ah         ; set pixel
	dec     cx
	jz      @@Done              ; Delta_Y--, Exit if Done

	add     si, bx              ; add numerator to accumulator
	jnc     @@STRnc3            ; if no carry then just go down...

	rol     al, 1               ; Move Right one addr if Plane = 0
	cmp     al, 12h             ; Wrap? if AL >12 then Carry not set
	adc     di, 0               ; Adjust Address: DI = DI + Carry
	out     dx, al              ; Set up New Bit Plane mask

    @@STRnc3:
	add     di, bp              ; advance to next line.
	jmp     @@STRLoop           ; loop till done

    ; !!DONE!!
    @@Done:
	pop     si di ds             ; Return state
	pop     bp
	ret

ENDP    _XPut_Line


; ------------------------ _MEvent_Handler ---------------------------
; - Xtile's mouse event handler
; - This one will NOT handle split screen
; -
PROC    _MEvent_Handler

    ; Save thier DS
	push ds di si

    ; This function is not callable from 'c' so we won't build a
    ; stack frame

    ; Save the event masks
	push    ax

    ; Correct X pos
	shr     cx,     1

    ; Save dx in SI
	mov     si,     dx

    ; Load DS.
	ASSUME  ds:  @fardata
	mov     ax,  @fardata
	mov     ds,  ax                 ; Set DS to fardata segment

    ; Check for Lock
	cmp     [LockHandler],  NOTLOCKM        ; Is it locked
	jz      @@NotLocked

		; !!!Locked!!!  Jump out
			pop             ax                                      ; Pull flags off stack
			jmp             @@Done

	@@NotLocked:
		; Lock the handler
			mov             ax,     LOCKM
			mov             [LockHandler], ax

	; Get and save the status of bit mask and map mask
		; Get status of bit mask
			mov             dx,     GC_INDEX
			mov             ax,     0808h     ; 08 is bit mask
			out             dx,     al
			inc             dx
			in              al,     dx              ; Get the bit mask
			xchg            al,     ah
			push            ax

		; Get Map Mask
			mov             ax,  0202h              ; 02 is Map Mask
			mov             dx,     SC_INDEX
			out             dx,     al
			inc             dx
			in              al,     dx
	       xchg             al,  ah
			push    ax

	; -- Restore the the current background location ---
	; This is a Save Site restore
			cld

		; Set DI to start of rectangle on screen
			mov             ax,  [DLine_Offset] ; Get line size
			mov             bx,  [MLastY]       ; Get Y into view
			push      bx                            ; Save on stack
			add          bx,  [ViewYLoc]     ; Add start of view
			mul             bx                         ; Find Y offset value. Place in AX

			mov             di,  [MLastX]     ; Find X offset value. Place in DI
			mov             bp,  di            ; Save X in BP
			add             di,  [ViewXLoc]   ; Get start of view and add it
			shr             di,  1
			shr             di,  1

			add             di,  ax            ; Add X and Y offsets
			add             di,  [MousePage]  ; Add in page offset

		; Get DX back
			mov             dx,     si

		; Save the new location as the last known
			mov             [MLastY],       dx
			mov             [MLastX],       cx

		; See if we need to clip Second XBlock
			mov             cx,             2                       ; Assume 2 XBlocks to write
			mov             dx,       [ScreenSizeX] ; Get screen size
			sub             dx,             4                       ; Get 2nd to last XBlock
			cmp             dx,             bp
			jae       @@Continue                    ; Both blocks on reload
			dec             cx                                      ; Only one block on reload

	@@Continue:
		; Set all data from latches
			mov             dx,     GC_INDEX
			mov             ax,     00000h + BIT_MASK  ; Set all latch writes
			out             dx,     ax

		; Insure Map Mask is set to all planes
			mov             ax,  0F02h      ; 0F sets all planes. 02 is Map Mask
			mov             dx,     SC_INDEX
			out             dx,     ax

		; Set DX to number of lines to display
			mov             dx,     8               ; Assume 8 scan lines
			pop             bx                      ; Get Y last and put in BX
			mov             ax,     [MouseLines] ; Get Screen size Y and put in ax
			sub             ax,     8               ; Subtract to make mimimum clip

			cmp             ax,     bx              ; Does it need clipping?
			ja              @@ContinueC     ; No, then go!

			sub             bx,  ax         ; Needs clipping!  Find new DX
			sub             dx,     bx

	@@ContinueC:
		; Set BP to the distance from one scan line to next
			mov             bp,     [DLine_Offset]
			sub             bp,  cx                 ; # XBlocks per scan line

		; Get the Screen Seg and put it in DS and ES
			mov             ax,  SCREEN_SEG
			mov             ds,  ax
			mov             es,     ax

		; Set SI to source
			mov             si,             MSAVEOFFSET

		; Save number to move (CX) in BX
			mov             bx,     cx

	   ; This section is for WRITES ------------------------------
	   ; The Reload will be clipped if appropriate

	 @@RowLoop:
		 ; Put 1 or 2 XBlocks
			rep movsb

		 ; Ready next iteration
			add             di,     bp      ; Point DI to start of next line

		 ; Reload CX
			mov             cx,     bx

		 ; Are we done?
			dec             dx
			jnz             @@RowLoop       ; No, then go and do another line


	; ------ Now save the new area --------
	; -- Do the reload

		; Load DS.
			ASSUME  ds:  @fardata
			mov             ax,  @fardata
			mov             ds,  ax                 ; Set DS to fardata segment

		; Set SI to start of rectangle on screen
			mov             ax,  [DLine_Offset] ; Get line size
			mov             bx,  [MLastY]       ; Get Y into view
			mov             di,     bx                      ; Save in DI
			add          bx,  [ViewYLoc]     ; Add start of view
			mul             bx                         ; Find Y offset value. Place in AX

			mov             si,  [MLastX]     ; Find X offset value. Place in DI
			mov             bp,  si            ; Save in BP for later
			add             si,  [ViewXLoc]   ; Get start of view and add it
			shr             si,  1
			shr             si,  1

			add             si,  ax            ; Add X and Y offsets
			add             si,  [MousePage]  ; Add in page offset

			push            si                              ; Save what will later be DI

		; See if we need to clip Second XBlock
			mov             cx,             2                       ; Assume 2 XBlocks to write
			mov             dx,       [ScreenSizeX] ; Get screen size
			sub             dx,             4                       ; Get 2nd to last XBlock
			cmp             dx,             bp             ; BP from above.
			jae        @@Continue3                  ; Both blocks on reload
			dec             cx                                      ; Only one block on reload

	@@Continue3:
		; Set DX to number of lines to reload
			mov             dx,     8               ; Assume 8 scan lines
			mov             bx,     di              ; Get Y last and put in BX (from SI)
			mov             ax,     [MouseLines] ; Get Screen size Y and put in ax
			sub             ax,     8               ; Subtract to make mimimum clip

			cmp             ax,     bx              ; Does it need clipping?
			ja              @@Continue4     ; No, then go!

			sub             bx,  ax         ; Needs clipping!  Find new DX
			sub             dx,     bx

	@@Continue4:
		; Save DX ( as number of lines to do ) for later
			push            dx

		; Set BP to scan line dif.
			mov             bp,     [DLine_Offset]
			sub             bp,  cx

		; Get the Screen Seg and put it in DS
			mov             ax,  SCREEN_SEG
			mov             ds,     ax

		; Set destination
			mov             di,     MSAVEOFFSET

		; Save SI so we dont have to calc later
			mov             ax,     si

		; Save CX in BX
			mov             bx,     cx

	   ; This section is for the MOVE ------------------------------

	@@RowLoop2:
		; Do line
			rep             movsb   ; One or two XBlocks

		; Prep for next line
			add             si,     bp      ; Point SI to start of next line
			mov             cx,     bx      ; Restore CX

		; Are we done?
			dec             dx
			jnz             @@RowLoop2      ; No, then go and do another


	; -------- Put the pointer in the new position ---------

		; Load DS.
			ASSUME  ds:  @fardata
			mov             ax,  @fardata
			mov             ds,  ax                 ; Set DS to fardata segment


		; Find adjustment to source for alignment
			mov             bx,     [MLastX]         ; Put X loc into cx
			add             bx,  [ViewXLoc] ; Add view start
			mov             ax,     16              ; Alignment size ( 2XBlocks*8Scan )

			and             bx,     3          ; Mask all but 2 LSBs
			mul             bx                 ; Find offset into alignments (Put in AX)

		; Put source into BX and add the alignment adj
			mov             bx,     MOUSEOFFSET
			add             bx,     ax         ; Adjust BX


		; Set DS:SI to mask area
			lds             si,     [MPointerMask]

		; Add adjustment for mask for alignment
			add             si,     ax         ; AX as calculated above

		; Set ah to rows to do
			pop             ax                 ; Was DX in the reload
			mov             ah,  al    ;    Move to upper nibble

		; Increment BP to account for no last inc
			inc             bp

		; Pop the destination, prev si, from the stack
			pop             di

		; Set up Map Mask
			mov             al,  02h        ; 02 is Map Mask
			mov             dx,     SC_INDEX
			out             dx,     al
			inc             dx

		; Shift CX down to see if we need to write 2nd XBlock
			shr             cx,     1

		; This section is for WRITES ------------------------------
	@@Rowloop3:


		; Set mask for first XBlock write
			lodsb                           ; Get mask byte
			out             dx,     al              ; Set map mask

		; Write the 1st XBlock
			mov             al,             [es:bx]
			mov             [es:di],        al

		; Load the next mask
			lodsb

		; Increment source pointers
			inc     bx

		; If CX is zero, pointer is clipped.  Don't put next XBlock
			jcxz            @@Clipped

	   ; - Do another write
		; Set the mask
			out             dx,     al              ; Set map mask

		; Increment destination pointers
			inc     di

		; Write the 2nd XBlock
			mov             al,             [es:bx]
			mov             [es:di],        al

	 @@Clipped:
		; Adjust for next iteration
			add             di,      bp  ; Point to next scan line
			inc             bx

		; End of Rowloop.
			dec             ah
			jnz             @@Rowloop3

	  ; -- Done with screen

	  ; Restore the state of the bit mask and map mask
		; Get Map Mask
			mov             dx,     SC_INDEX
			pop             ax
			out             dx,     ax

		; Do the BIT MASK
			mov             dx,     GC_INDEX
			pop             ax
			out             dx,     ax

	  ; -- See if we have to call the user's handler

		; Make ES the local data
			ASSUME  es:  @fardata
			mov             ax,  @fardata
			mov             es,  ax                 ; Set ES to fardata segment

		; Restore event masks
			pop             ax

		; AND the mask with users mask
			and             ax,     [es:MUEventMask]

		; If zero then don't call user's
			je              @@Done

		; Otherwise set up stack and call users routine
			push            ax           ; Push the event mask

			mov     ax,  @data           ; Load users DGROUP
			mov     ds,  ax

			push            [es:MLastY]  ; Push Y location

			push            [es:MLastX]  ; Push X location

			call    [es:Mouse_Hndlr_O]      ; User pointer to call user handler

					; Clean stack after call
			add             sp,     6


	  ; -- DONE!!

	@@Done:
		; Make DS the local data
			ASSUME  ds:  @fardata
			mov             ax,  @fardata
			mov             ds,  ax                 ; Set DS to fardata segment

		; Unlock the handler
			mov             ax,     NOTLOCKM
			mov             [LockHandler], ax

			pop   si di ds     ; Pop mouses DS
			ret

ENDP                    _MEvent_Handler


; ------------------------ _XRegister_Mouse ---------------------------
; - This function sets up the use of the mouse
; - It returns FALSE (0) if it cannot
; -
public          _XRegister_Mouse

PROC                    _XRegister_Mouse

	ARG     MPOINT:DWORD, MMASK:DWORD, HANDLER:DWORD

			push            bp
			mov             bp,     sp              ; Save Stack frame

		; Save registers
			push            ds si di

		; First see if mouse is available
			mov       ax,   0
			int             33h

			cmp             ax,     0FFFFh  ; If ax is FFFFh then mouse is present
			je              @@Cont

			mov             ax,     MOUSE_NOTPRES
			jmp             @@Done          ; Otherwise, done

		; Mouse is present so continue
	@@Cont:

	; ----------- Upload mouse pointer ------------

		; Prep the stack for call to upload sprite
			les             si,     [MPOINT]        ; Get the image offset
			push            es
			push            si

			mov             si,     (64*4)          ; Mouse sprite size
			push            si

			mov             si,     MOUSEOFFSET    ; Mouse pointer site
			push            si

		; Call upload sprite
			call    _XUpload_Sprite

		; Fix the stack after call
			add             sp,     8

	; ---- Done uploading mouse pointer


		; Load DS.
			ASSUME  ds:  @fardata
			mov             ax,  @fardata
			mov             ds,  ax                 ; Set DS to fardata segment

		; Get the pointer mask address
			les             si,  [MMASK]
			mov             [MPointerMask_O], si
			mov             ax,  es
			mov             [MPointerMask_S], ax

		; Set up user handler
			les             si,  [HANDLER]
			mov             [OFFSET Mouse_Hndlr_O], si
			mov             ax,  es
			mov             [OFFSET Mouse_Hndlr_O+2], ax

		; !!DONE!!
			mov             ax,     MOUSE_PRESENT   ; Return the mouse is present


		; Set the new mickey counts
			mov             ax,     000Fh            ; Mouse driver routine
			mov             cx,  8                   ; Double horizontal
			mov             dx,  8                   ; Default vertical
			int             033h                             ; Call routine

	@@Done:
			pop             di si ds

			pop             bp                      ; Return state
	       ret

ENDP                    _XRegister_Mouse


; ------------------------ _Mouse_Active ---------------------------
; - Makes the mouse active and starts display of the pointer
; - It will not process for the split screen crossover, but can decided
; - between split page and normal page

public  _XMouse_Active

PROC    _XMouse_Active

      ARG       EMASK:WORD, STARTX:WORD, STARTY:WORD, SPLIT:WORD

	push    bp
	mov     bp,     sp              ; Save Stack frame

    ; Save regs
	push    ds si di

    ; Load DS.
	ASSUME  ds:  @fardata
	mov     ax,  @fardata
	mov     ds,  ax                 ; Set DS to fardata segment

	cld

    ; Save the users mask
	mov     cx,     [EMASK]         ; Get user's mask
	push    cx

    ; Which page ?
	mov     cx,     [SPLIT]
	jcxz    @@NORMALPAGE

  ; - Split Page
	mov     ax,             0
	mov     [MousePage],    ax      ; Set page to 0, split page

	mov     ax,   [OriginalY]       ; Get absolute size
	sub     ax,   [SplitY]          ; Subtract split Y size
	mov     [MouseLines],   ax      ; Save it as the number of lines

	jmp     @@CONTINUE

  ; - Normal Page
  @@NORMALPAGE:
	mov     ax,          [Display_Page]
	mov     [MousePage], ax                 ; Set to display page

	mov     ax,   [ScreenSizeY]     ;
	mov     [MouseLines],   ax      ; Save it as the number of lines

    ; Set mouse reporting limits
  @@CONTINUE:
	mov     ax,  0007h      ; Set X limits function
	mov             cx,     0000h   ; X minimum
			mov       dx,  [ScreenSizeX]       ; X maximum
			dec             dx
			shl             dx,     1            ; Comp for oddity
			int             033h

			mov             ax,     0008h      ; Set Y limits function
			mov             cx,     0000h        ; Y minimum
	mov     dx,     [MouseLines]  ; Y maximum
	dec     dx                     ; Adjust for pointer size
	int       033h

	; -- Set up VGA Registers
		; Set all data from latches
			mov             dx,     GC_INDEX
			mov             ax,     00000h + BIT_MASK  ; Set all latch writes
			out             dx,     ax

		; Insure Map Mask is set to all planes
			mov             ax,  0F02h      ; 0F sets all planes. 02 is Map Mask
			mov             dx,     SC_INDEX
			out             dx,     ax


	; -- Set the mouse's position and display the pointer

		; Let the driver know where the mouse pointer is
			mov             ax,     0004h   ; Set mouse pointer position func

			mov             cx,     [STARTX]        ; Set X coord
			mov             dx,     [STARTY]        ; Set Y coord

			mov       [MLastX], cx   ; Save as last location
			mov             [MLastY], dx

			shl             cx,     1               ; Comp

			int             033h                    ; Call function

		; Return CX to actual coord
			shr             cx,     1



		; Reload background & fake a mouse event to put the pointer
		; on the screen the first time

	; -- Do the reload ------------------------------

		; Set SI to start of rectangle on screen
			mov             ax,  [DLine_Offset] ; Get line size
			mov             bx,  [MLastY]       ; Get Y into view
			mov             di,     bx                      ; Save in DI
			add          bx,  [ViewYLoc]     ; Add start of view
			mul             bx                         ; Find Y offset value. Place in AX

			mov             si,  [MLastX]     ; Find X offset value. Place in DI
			mov             bp,  si            ; Save in BP for later
			add             si,  [ViewXLoc]   ; Get start of view and add it
			shr             si,  1
			shr             si,  1

			add             si,  ax              ; Add X and Y offsets
			add             si,  [MousePage]    ; Add in page offset

		; See if we need to clip Second XBlock
			mov             cx,             2                       ; Assume 2 XBlocks to write
			mov             dx,       [ScreenSizeX] ; Get screen size
			sub             dx,             4                       ; Get 2nd to last XBlock
			cmp             dx,             bp             ; BP from above.
			jge       @@Continue3                   ; Both blocks on reload
			dec             cx                                      ; Only one block on reload

	@@Continue3:
		; Set DX to number of lines to reload
			mov             dx,     8               ; Assume 8 scan lines
			mov             bx,     di              ; Get Y last and put in BX (from SI)
			mov             ax,     [MouseLines] ; Get Screen size Y and put in ax
			sub             ax,     8               ; Subtract to make mimimum clip

			cmp             ax,     bx              ; Does it need clipping?
			jg              @@Continue4     ; No, then go!

			sub             bx,  ax         ; Needs clipping!  Find new DX
			sub             dx,     bx

	@@Continue4:

		; Set BP to scan line dif.
			mov             bp,     [DLine_Offset]
			sub             bp,  cx

		; Get the Screen Seg and put it in DS and ES
			mov             ax,  SCREEN_SEG
			mov             ds,     ax
			mov             es,  ax

		; Set destination
			mov             di,     MSAVEOFFSET

		; Save CX in BX
			mov             bx,     cx

	   ; This section is for the MOVE ------------------------------

	@@RowLoop3:
		; Do line
			rep             movsb   ; One or two XBlocks

		; Prep for next line
			add             si,     bp      ; Point SI to start of next line
			mov             cx,     bx      ; Restore CX

		; Are we done?
			dec             dx
			jnz             @@RowLoop3      ; No, then go and do another


	  ; - Done with Reload ---------------------------------------

		; Load DS.
			ASSUME  ds:  @fardata
			mov             ax,  @fardata
			mov             ds,  ax                 ; Set DS to fardata segment

		; Ensure the mouse is not locked
			mov             ax,                     NOTLOCKM
			mov             [LockHandler], ax

		; Fake a mouse event and call handler
			mov             ax,     0001h   ; Indicate a move
			mov             bx,     0000h   ; No info
			mov             cx,  [MLastX]  ; X coord
			shl             cx,     1               ; Double, that's how it is expected
			mov             dx,     [MLastY]  ; Y coord

			call            _MEvent_Handler

		; Get the users event mask, save, and build our mask in cx
			pop             cx                              ; Get the Mask back
			mov             [MUEventMask], cx   ; Save it
			or              cx,  0001h          ; Build ours
						; We must make sure all moves are reported

		; Define XTile's handler and register it with the mouse driver
			mov             ax,     0000Ch  ; Function number
			mov             dx,     cs              ; Get XTile's handler address
			mov             es,     dx
			mov             dx,  OFFSET _MEvent_Handler

			int             033h                    ; Call the mouse driver
		; !!DONE!!

			pop             di si ds                ; Restore regs

			pop             bp                      ; Return state
			ret

ENDP                    _XMouse_Active

; ------------------------ _XMouse_InActive ---------------------------
; - Turns off the mouse.  Restores background to last known mouse position.
; -
public          _XMouse_InActive

PROC                    _XMouse_InActive

			push            bp
			mov             bp,     sp              ; Save Stack frame

		; Save regs
			push            ds si di

		; Register with mouse driver that we don't want any more reports
			mov             ax,     0000Ch  ; Function number
			mov             cx,     00000h  ; Clear flag
			mov             dx,     00000h  ; Get XTile's handler address
			mov             es,     dx
			mov             dx,  00000h

			int             033h                    ; Call the mouse driver

		; Load DS with local segment
			ASSUME  ds:  @fardata
			mov             ax,  @fardata
			mov             ds,  ax         ; Set DS to fardata segment

		; Ensure the mouse is locked
			mov             ax,                     LOCKM
			mov             [LockHandler], ax

		; Clear the users mask
			mov             cx,     0000h                   ; Set to no events
			mov             [MUEventMask], cx   ; Save it

	; ---- This is the restore
			cld

		; Set DI to start of rectangle on screen
			mov             ax,  [DLine_Offset] ; Get line size
			mov             bx,  [MLastY]       ; Get Y into view
			mov             si,  bx                         ; Save in SI
			add          bx,  [ViewYLoc]     ; Add start of view
			mul             bx                         ; Find Y offset value. Place in AX

			mov             di,  [MLastX]     ; Find X offset value. Place in DI
			mov             bp,  di            ; Save X in BP
			add             di,  [ViewXLoc]   ; Get start of view and add it
			shr             di,  1
			shr             di,  1

			add             di,  ax            ; Add X and Y offsets
			add             di,  [MousePage]  ; Add in page offset


		; See if we need to clip Second XBlock
			mov             cx,             2                       ; Assume 2 XBlocks to write
			mov             dx,       [ScreenSizeX] ; Get screen size
			sub             dx,             4                       ; Get 2nd to last XBlock
			cmp             dx,             bp
			ja        @@Continue                    ; Both blocks on reload
			dec             cx                                      ; Only one block on reload

	@@Continue:
		; Set all data from latches
			mov             dx,     GC_INDEX
			mov             ax,     00000h + BIT_MASK  ; Set all latch writes
			out             dx,     ax

		; Insure Map Mask is set to all planes
			mov             ax,     0F02h    ; 0F sets all planes. 02 is Map Mask
			mov             dx,     SC_INDEX
			out             dx,     ax

		; Set DX to number of lines to display
			mov             dx,     8            ; Assume 8 scan lines
			mov             ax,     [MouseLines] ; Get Screen size Y and put in ax
			sub             ax,     8            ; Subtract to make mimimum clip

			cmp             ax,     si      ; Does it need clipping? (SI = last Y)
			ja              @@ContinueC     ; No, then go!

			sub             si,  ax         ; Needs clipping!  Find new DX
			sub             dx,     si

	@@ContinueC:
		; Set BP to the distance from one scan line to next
			mov             bp,     [DLine_Offset]
			sub             bp,  cx                 ; # XBlocks per scan line

		; Get the Screen Seg and put it in DS and ES
			mov             ax,  SCREEN_SEG
			mov             ds,  ax
			mov             es,     ax

		; Set SI to source
			mov             si,             MSAVEOFFSET

		; Save number to move (CX) in BX
			mov             bx,     cx

	   ; This section is for WRITES ------------------------------
	   ; The Reload will be clipped if appropriate

	 @@RowLoop:
		 ; Put 1 or 2 XBlocks
			rep movsb

		 ; Ready next iteration
			add             di,     bp      ; Point DI to start of next line

		 ; Reload CX
			mov             cx,     bx

		 ; Are we done?
			dec             dx
			jnz             @@RowLoop       ; No, then go and do another line


	   ; -- DONE!!

			pop             di si ds                ; Restore regs
			pop             bp                      ; Return state
	       ret

ENDP                    _XMouse_InActive


; ------------------------ _XWhere_Mouse ---------------------------
; - Lets the user know the last known mouse location
public  _XWhere_Mouse

PROC    _XWhere_Mouse   FAR

	ARG     X:DWORD, Y:DWORD

	push    bp
	mov     bp,     sp              ; Save Stack frame
	push    ds si

      ; Load DS.
	ASSUME  ds:  @fardata
	mov     ax,  @fardata
	mov     ds,  ax                 ; Set DS to fardata segment

      ; Load pointer to X
        les     si,  [X]

      ; Get value and save
        mov     ax,      [MLastX]
        mov     [es:si], ax

      ; Load pointer to Y
        les     si,  [Y]

      ; Get value and save
        mov     ax,      [MLastY]
        mov     [es:si], ax

      ; !!DONE!!
	pop     si ds
	pop     bp                      ; Return state
	ret

ENDP    _XWhere_Mouse


; ------------------------ _XReload_TileW ---------------------------
; - This will "reload" a tile from the write page to display memory
; - storeage
; -
public          _XReload_TileW

PROC                    _XReload_TileW

	ARG     STARTX:WORD, STARTY:WORD, XDELTA:WORD, YDELTA:WORD, OFFST:WORD

			push            bp
			mov             bp,     sp              ; Save Stack frame

		; Save regs
			push            ds si di

		; Load DS.
			ASSUME  ds:  @fardata
			mov             ax,  @fardata
			mov             ds,  ax                 ; Set DS to fardata segment

	; -- Do the reload

		; Set SI to start of rectangle on screen
			mov             ax,  [WLine_Offset] ; Get line size
			mul             [STARTY]                        ; Find Y offset

			mov             si,  [STARTX]     ; Find X offset value. Place in DI
			shr             si,  1
			shr             si,  1

			add             si,  ax              ; Add X and Y offsets
			add             si,  [Write_Page]   ; Add in page offset

		; Set destination
			mov             di,     [OFFST]

		; Set all data from latches
			mov             dx,     GC_INDEX
			mov             ax,     00000h + BIT_MASK  ; Set all latch writes
			out             dx,     ax

		; Insure Map Mask is set to all planes
			mov             ax,  0F02h      ; 0F sets all planes. 02 is Map Mask
			mov             dx,     SC_INDEX
			out             dx,     ax

		; Set CX to the number of XBlocks to move per scan line
			mov       cx,  [XDELTA]
			shr             cx,  1
			shr             cx,  1

		; Set BX to the number of scan lines to do
			mov             bx,  [YDELTA]

		; Set AX to scan line dif.
			mov             ax,     [WLine_Offset]
			sub             ax,  cx                 ; Scan size minus tile size

		; Get the Screen Seg and put it in ES
			mov             dx,  SCREEN_SEG
			mov             es,     dx
			mov             ds,     dx

		; Save number of XBlocks (CX) in DX
			mov             dx,  cx

		; This section is for the MOVE ------------------------------
		@@Rowloop:

			rep  movsb

		; Adjust for next iteration
			add     si,     ax      ; Point DI to start of next line

		; Re-load CX
			mov  cx,  dx

		; End of Rowloop.
			dec       bx
			jnz    @@Rowloop


		; DONE!!!
			pop    di si ds

			pop       bp                    ; Return state
			ret

ENDP       _XReload_TileW


; ------------------------ _XDownload_TileS ---------------------------
; - This will "download" a tile from a store in video mem to system memory
; - This will not correctly download from an active screen!
; - WARNING!!! Only download one segments worth at a time!!!!
; -
public  _XDownload_TileS

PROC    _XDownload_TileS

	ARG     STOREA:WORD, SIZEX:WORD, SIZEY:WORD, DEST:DWORD

	push    bp
	mov     bp,     sp              ; Save Stack frame

      ; Save regs
	push    ds si di

      ; Set SI to the store position
	mov     si,     [STOREA]
	mov     bx,     si              ; Save in BX

      ; Load destination
	les     di,     [DEST]

      ; Calculate bytes per plane and store in CX
	mov     ax,     [SIZEX]
	mul     [SIZEY]                 ; Total bytes
	shr     ax,     1
	shr     ax,     1               ; Total XBlocks
	mov     cx,     ax
	mov     bp,     ax              ; Save in BP

     ; Set up store segment
	mov     ax,     SCREEN_SEG
	mov     ds,     ax

      ; Set up graphics controller for read page
	mov     dx,     GC_INDEX
	mov     al,     04h     ; Read map register
	out     dx,     al
	inc     dx

      ; Set AL to plane access
	mov     al,     00h

      ; Set AH to planes left
	mov     ah,     4

    ; This section is for the move ------------------------------
    @@PlaneLoop:

      ; Set MAP MASK
	out     dx,     al

      ; Do the move
	shr     cx,     1       ; Shift for word
	rep     movsw
	adc     cx,     0       ; Add 1(carry) to CX if dangling byte
	rep     movsb           ; Write dangle if there

      ; Restore CX and source
	mov     cx,     bp
	mov     si,     bx

      ; !!End of Plane loop.
	inc     al
	dec     ah
	jnz     @@PlaneLoop

      ; DONE!!!
	pop     di si ds
	pop     bp                      ; Return state
	ret

ENDP       _XDownload_TileS


; ------------------------ _XDownload_TileP ---------------------------
; - This will "download" a tile from the write page in video mem to system memory
; - WARNING!!! Only download one segments worth at a time!!!!
; -
public  _XDownload_TileP

PROC    _XDownload_TileP

	ARG     STARTX:WORD, STARTY:WORD, SIZEX:WORD, SIZEY:WORD, DEST:DWORD

	push    bp
	mov     bp,     sp          ; Save Stack frame

      ; Save regs
	push    ds si di

      ; Load DS.
	ASSUME  ds:  @fardata
	mov     ax,  @fardata
	mov     ds,  ax             ; Set DS to fardata segment

      ; Set SI to start of rectangle on screen
	mov     ax,  [WLine_Offset] ; Get line size
	mul     [STARTY]            ; Find Y offset

	mov     si,  [STARTX]       ; Find X offset value. Place in DI
	push    si                  ; Save it
	shr     si,  1
	shr     si,  1

	add     si,       ax             ; Add X and Y offsets
	add     si,       [Write_Page]   ; Add in page offset
	mov     [STARTX], si             ; Save SI

      ; Load destination
	les     di,     [DEST]

      ; Calculate bytes per line and store in CX
	mov     cx,     [SIZEX]
	shr     cx,     1
	shr     cx,     1               ; Total XBlocks

      ; Set [STARTY] to scan line dif.
	mov     ax,       [WLine_Offset]
	sub     ax,       cx            ; Scan size minus tile size
	mov     [STARTY], ax

      ; Set up store segment
	mov     ax,     SCREEN_SEG
	mov     ds,     ax

      ; Set up graphics controller for read page
	mov     dx,     GC_INDEX
	mov     al,     04h     ; Read map register
	out     dx,     al
	inc     dx

      ; Set AL to plane access
	pop     ax
	and     al,     03h             ; Mask out plane selecting bits

      ; Save XBlocks per line (CL) in AH
	mov     ah,     cl

      ; Set BX to number of lines to do
	mov     bx,     [SIZEY]

      ; Set number of planes to do
	mov     [SIZEX],  4

    ; This section is for the move ------------------------------
    @@PlaneLoop:

      ; Set READ MASK
	out     dx,     al

      ; Steal dx to speed up add
	mov     dx,     [STARTY]

    @@LineLoop:
      ; Do the move
	shr     cx,     1       ; Shift for word
	rep     movsw
	adc     cx,     0       ; Add 1(carry) to CX if dangling byte
	rep     movsb           ; Write dangle if there

      ; Adjust for next line
	add     si,     dx      ; Next line
	mov     cl,     ah      ; Reset items per line

      ; End of work for this plane?
	dec     bx
	jnz     @@LineLoop

      ; Transition back to plane 0?
	inc     al
	cmp     al,     4       ; Plane 3 is last valid
	jne     @@Continue
	mov     al,     0       ; Put back to planer 0
	inc     [STARTX]        ; Move to next XBlock

    @@Continue:
      ; Adjust for another plane
	mov     si,     [STARTX]        ; Reset source
	mov     bx,     [SIZEY]         ; Reset number of lines

      ; Next plane?
	mov     dx,     GC_INDEX+1      ; Restore DX
	dec     [SIZEX]
	jnz     @@PlaneLoop

      ; DONE!!!
	pop     di si ds
	pop     bp                      ; Return state
	ret

ENDP    _XDownload_TileP


; ------------------------ _XSet_Clip ---------------------------
; - This will set the clip boundries
; -
public          _XSet_Clip

PROC                    _XSet_Clip

	ARG     STARTX:WORD, STARTY:WORD, ENDX:WORD, ENDY:WORD

			push            bp
			mov             bp,     sp              ; Save Stack frame

		; Save regs
			push            ds

		; Load DS.
			ASSUME  ds:  @fardata
			mov             ax,  @fardata
			mov             ds,  ax                 ; Set DS to fardata segment

	; -- Load the values
			mov             ax,             [STARTX]
			mov             [ClipSX],       ax
			mov             ax,             [STARTY]
			mov             [ClipSY],       ax
			mov             ax,             [ENDX]
			mov             [ClipEX],       ax
			mov             ax,             [ENDY]
			mov             [ClipEY],       ax

		; DONE!!!
			pop    ds

			pop       bp                    ; Return state
			ret

ENDP       _XSet_Clip


ENDS

END
