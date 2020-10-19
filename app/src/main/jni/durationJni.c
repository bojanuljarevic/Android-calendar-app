//
// Created by bojan on 31.5.20..
#include "durationJni.h"


JNIEXPORT jint JNICALL Java_bojan_uljarevic_calendarapp_DurationNDK_DurationCalculate(JNIEnv *env, jobject obj, jstring t1, jstring t2) {

    int start = 0;
    int end = 0;
    const char* str1 = (*env)->GetStringUTFChars(env, t1, NULL);
    const char* str2 = (*env)->GetStringUTFChars(env, t2, NULL);


    if(str1[4] == '\0') {
        start += (str1[0] - '0') * 60;
        start += (str1[2] - '0') * 10;
        start += (str1[3] - '0');
    } else {
        start += (str1[0] - '0') * 60 * 10;
        start += (str1[1] - '0') * 60;
        start += (str1[3] - '0') * 10;
        start += (str1[4] - '0');
    }

    if(str2[4] == '\0') {
        end += (str2[0] - '0') * 60;
        end += (str2[2] - '0') * 10;
        end += (str2[3] - '0');
    } else {
        end += (str2[0] - '0') * 60 * 10;
        end += (str2[1] - '0') * 60;
        end += (str2[3] - '0') * 10;
        end += (str2[4] - '0');
    }

    (*env)->ReleaseStringUTFChars(env, t1, str1);
    (*env)->ReleaseStringUTFChars(env, t2, str2);


    if (end - start > 0) return end - start;
    else return 0;
}
//

