#include <jni.h>
#include <string>
#include <fcntl.h>
#include <sys/ioctl.h>
#include <unistd.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <android/log.h>
#include "scdefine.h"

/////////////////////////////////////////////////////


///////////////////////////////////////////////////
unsigned int atoHex(unsigned char *tmpstr)
{
    int si,i;
    unsigned int ret = 0;
    unsigned char c;
    if(tmpstr[0]=='0' && (tmpstr[1]=='x'||tmpstr[1]=='X')){
        si = 2;
    }else{
        si = 0;
    }
    for(i=si;i<10;i++){
        c = tmpstr[i];
        if(c>='0' && c<='9'){
            ret = (ret<<4)|(c-'0');
        }else if(c>='a' && c<='f'){
            ret = (ret<<4)|(c-'a'+10);
        }else if(c>='A' && c<='F'){
            ret = (ret<<4)|(c-'A'+10);
        }else {
            break;
        }
    }
    return ret;
}
unsigned char get_strbuf_CRC(char *pRet, int len)
{
	int i;
	unsigned int CRC=0;
	unsigned char *pUChar;
	pUChar     = (unsigned char *)pRet;
	for(i=0;i<len;i++){
	    if(pUChar[i]==0)break;
		CRC+=pUChar[i];
	}
	CRC = (0x100-(CRC&0xFF))&0xFF;
	//printf("CRC=0x%02x\n",CRC);
	return (unsigned char)CRC;
}
//0  ~ no data
//-1 ~ data error
//1  ~ data ok
int check_bd_data(char *pBuf)
{
	int i,len;
	char c;
	unsigned char CRC,tCRC;
	unsigned char tstr[32];
	if(pBuf==NULL)return 0;
	for(i=0;i<1024;i++){
		c = pBuf[i];
		if(c>='0' && c<='9')continue;
		if(c>='a' && c<='z')continue;
		if(c>='A' && c<='Z')continue;
		break;
	}
	if(i<=5){
		LOGD("CPP:no data\n");
		return -1;
	}
	memcpy(tstr,&pBuf[2],3);tstr[3]=0;
	len = atoHex(tstr);
	if(len<=5 || len>=1024){
		LOGD("CPP:data error\n");
		return -1;
	}
	memcpy(tstr,&pBuf[0],2);tstr[2]=0;
	tCRC = atoHex(tstr);
	CRC  = get_strbuf_CRC(&pBuf[5], len-5);
	//LOGD("CPP:len=%d CRC=%02x tCRC=%02x\n",lenX, CRC,tCRC);
	if(tCRC!=CRC){
		LOGD("CPP:CRC error\n");
		return -1;
	}
	return 1;
}