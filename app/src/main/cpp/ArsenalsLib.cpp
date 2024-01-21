//
// Created by e4256 on 2023/3/1.
//
#include <jni.h>
#include <string>
#include <android/log.h>

#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE,"ARSENALS_JNI",__VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,"ARSENALS_JNI", __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,"ARSENALS_JNI",__VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,"ARSENALS_JNI",__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,"ARSENALS_JNI",__VA_ARGS__)
#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL,"ARSENALS_JNI",__VA_ARGS__)

std::string Jstring2String(JNIEnv *env, jstring str) {
    if (str == nullptr) {
        return "";
    }
    const char *kstr = env->GetStringUTFChars(str, nullptr);
    if (kstr == nullptr) {
        return "";
    }
    std::string result(kstr);
    env->ReleaseStringUTFChars(str, kstr);
    return result;
}

void ReadStringFromFile(const char* path, char* out) {
    FILE *file = fopen(path, "r");
    if (file == nullptr) {
        return;
    }
    fscanf(file, "%s", out);
    fclose(file);
}

void ReadStringFromCmd(const char* cmd, char* out) {
    FILE *file = popen(cmd, "r");
    if (file == nullptr) {
        return;
    }
    fscanf(file, "%s", out);
    fclose(file);
}

std::string GetCpuUtilizationStr() {
    std::string ret = "";
    const int idleTimeIndex = 4 - 1;
    char value[255];
    bool isFirstValue = true;

    FILE *file = fopen("/proc/stat", "r");
    if (file == nullptr) {
        return ret;
    }

    while (true) {
        if (fscanf(file, "%s", value) == EOF) {
            fclose(file);
            return ret;
        }

        // if value contains "cpu", read the next 7 value, the idleTime is the forth, index is 4 -1 = 3
        if (strstr(value, "cpu")) {
            long totalTime = 0L;
            for (int i = 0; i < 7; i++) {
                fscanf(file, "%s", value);
                if (i == idleTimeIndex) {
                    if (isFirstValue) {
                        isFirstValue = false;
                    } else {
                        ret += ",";
                    }
                    ret += value;
                }
                long valueLong = strtol(value, nullptr, 10 /* decimal */);
                totalTime += valueLong;
            }
            ret += ",";
            ret += std::to_string(totalTime);
        }

        // when read to intr, the cpu info is end
        if (strcmp(value, "intr") == 0) {
            fclose(file);
            return ret;
        }
    }
}

extern "C" JNIEXPORT jstring JNICALL Java_cn_arsenals_osarsenals_jni_ArsenalsJni_stringFromJNI(
        JNIEnv* env,
        jobject /* this */,
        jstring input) {
    std::string inStr = Jstring2String(env, input);
    std::string hello = inStr + "|Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jstring JNICALL Java_cn_arsenals_osarsenals_jni_ArsenalsJni_getStringValFromFile(
    JNIEnv *env, jobject, jstring path) {
    std::string pathStr = Jstring2String(env, path);
    char ret[255] = {0};
    ReadStringFromFile(pathStr.c_str(), ret);
    return env->NewStringUTF(ret);
}

extern "C" JNIEXPORT jstring JNICALL Java_cn_arsenals_osarsenals_jni_ArsenalsJni_getStringValFromCmd(
    JNIEnv *env, jobject, jstring cmd) {
    std::string cmdStr = Jstring2String(env, cmd);
    char ret[255] = {0};
    ReadStringFromCmd(cmdStr.c_str(), ret);
    return env->NewStringUTF(ret);
}

extern "C" JNIEXPORT jstring JNICALL Java_cn_arsenals_osarsenals_jni_ArsenalsJni_getCpuUtilizationStr(
    JNIEnv *env, jobject) {
    return env->NewStringUTF(GetCpuUtilizationStr().c_str());
}

extern "C" JNIEXPORT jstring JNICALL Java_cn_arsenals_osarsenals_jni_ArsenalsJni_getAioStatus(
    JNIEnv *env, jobject) {
    std::string ret = "";
    return env->NewStringUTF(ret.c_str());
}
