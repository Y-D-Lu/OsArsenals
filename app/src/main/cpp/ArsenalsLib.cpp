//
// Created by e4256 on 2023/3/1.
//
#include <jni.h>
#include "dirent.h"
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

void ReadStringFromFile(const char *path, char *out) {
    FILE *file = fopen(path, "r");
    if (file == nullptr) {
        return;
    }
    fscanf(file, "%s", out);
    fclose(file);
}

void ReadStringFromCmd(const char *cmd, char *out) {
    FILE *file = popen(cmd, "r");
    if (file == nullptr) {
        return;
    }
    fscanf(file, "%s", out);
    fclose(file);
}

std::string GetCpuUtilizationStr() {
    std::string ret;
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
        JNIEnv *env,
        jclass clazz,
        jstring input) {
    std::string inStr = Jstring2String(env, input);
    std::string hello = inStr + "|Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_cn_arsenals_osarsenals_jni_ArsenalsJni_getStringValFromFile(
        JNIEnv *env, jclass clazz, jstring path) {
    std::string pathStr = Jstring2String(env, path);
    char ret[255] = {0};
    ReadStringFromFile(pathStr.c_str(), ret);
    return env->NewStringUTF(ret);
}

extern "C" JNIEXPORT jstring JNICALL
Java_cn_arsenals_osarsenals_jni_ArsenalsJni_getStringValFromCmd(
        JNIEnv *env, jclass clazz, jstring cmd) {
    std::string cmdStr = Jstring2String(env, cmd);
    char ret[255] = {0};
    ReadStringFromCmd(cmdStr.c_str(), ret);
    return env->NewStringUTF(ret);
}

extern "C" JNIEXPORT jstring JNICALL
Java_cn_arsenals_osarsenals_jni_ArsenalsJni_getCpuUtilizationStr(
        JNIEnv *env, jclass clazz) {
    return env->NewStringUTF(GetCpuUtilizationStr().c_str());
}

extern "C" JNIEXPORT jstring JNICALL Java_cn_arsenals_osarsenals_jni_ArsenalsJni_getAioStatus(
        JNIEnv *env, jclass clazz) {
    std::string ret;
    return env->NewStringUTF(ret.c_str());
}

//extern "C"
//JNIEXPORT jstring JNICALL
//Java_cn_arsenals_osarsenals_jni_ArsenalsJni_getHighestTemperature(JNIEnv *env, jclass clazz) {
//    int tmpHighestTemp = -273000;
//    std::string pathStr = "/sys/class/thermal/thermal_zone34/temp";
//    char ret[255] = {0};
//    ReadStringFromFile(pathStr.c_str(), ret);
//    int valueInt = strtol(ret, nullptr, 10 /* decimal */);
//    if (valueInt > tmpHighestTemp) {
//        tmpHighestTemp = valueInt;
//    }
//    return env->NewStringUTF(std::to_string(tmpHighestTemp).c_str());
//}
extern "C"
JNIEXPORT jint JNICALL
Java_cn_arsenals_osarsenals_jni_ArsenalsJni_getHighestTemperature(JNIEnv *env, jclass clazz) {
    int tmpHighestTemp = -273000;
    std::string folderPathStr = "/sys/class/thermal/";
    DIR *dir = opendir(folderPathStr.c_str());
    if (dir == nullptr) {
        return reinterpret_cast<jint>(0);
    }
    dirent *entry;
    while ((entry = readdir(dir)) != nullptr) {
        if (!(entry->d_type & DT_DIR || entry->d_type & DT_LNK)) {
            continue;
        }
        const char *fileName = entry->d_name;
        if (std::string(fileName).find("thermal_zone") == std::string::npos) {
            continue;
        }
        std::string filePathStr = folderPathStr + fileName + "/temp";
        char ret[255] = {0};
        ReadStringFromFile(filePathStr.c_str(), ret);
        int valueInt = strtol(ret, nullptr, 10 /* decimal */);
        if (valueInt > tmpHighestTemp) {
            tmpHighestTemp = valueInt;
        }
    }
    closedir(dir);
    return reinterpret_cast<jint>(tmpHighestTemp);
}
extern "C"
JNIEXPORT jint JNICALL
Java_cn_arsenals_osarsenals_jni_ArsenalsJni_getGpuBusy(JNIEnv *env, jclass clazz) {
    int ret = -1;
    char value[255];
    FILE *file = fopen("/sys/class/kgsl/kgsl-3d0/gpubusy", "r");
    if (file == nullptr) {
        return ret;
    }

    int used = 0;
    int total = 0;
    if (fscanf(file, "%s", value) == EOF) {
        fclose(file);
        return ret;
    }
    used = strtol(value, nullptr, 10 /* decimal */);
    if (fscanf(file, "%s", value) == EOF) {
        fclose(file);
        return ret;
    }
    total = strtol(value, nullptr, 10 /* decimal */);
    ret = round(used * 100.0 / total);

    fclose(file);
    return ret;
}