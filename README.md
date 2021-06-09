# File-Transfer-With-UDP
The sender module will run and open a file on its own hard disk and send the file contents in blocks to the receiver module.

- The receiving module will save the incoming file to its own hard disk.

- Each block will contain 100 bytes.

- 1 byte length value will be sent as the block header. (So the total packet size will be 101 bytes)

- The receiver will send an ACK for every block it reaches. The content of the ACK packet will be a string of 101 bytes consisting entirely of 0x7E bytes.
 
- Sender will not send new block unless ACK comes from receiver.

- When a packet smaller than 100 bytes arrives, the receiving party will realize that the end of the file has been reached and will close the file and the program. (To avoid problems, do not use a file that is a multiple of 100)

- When the transfer is finished, the file created on the receiving side must have exactly the same content as the file on the sender side. For example, the files on both sides must be equal in size. While transferring the image file, its image should be intact when we open the file.
