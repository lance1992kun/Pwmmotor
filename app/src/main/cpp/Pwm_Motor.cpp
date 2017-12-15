//
// Created by syk on 2017/7/31.
//

#include <jni.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <termios.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

#include <android/log.h>

#define LOG_TAG "Pwm_Motor-JNI"
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define LOGF(...)  __android_log_print(ANDROID_LOG_FATAL,LOG_TAG,__VA_ARGS__)

typedef struct {
    jint pwm_id;
    jint duty_ns;
    jint period_ns;
} pwmmotor_ctl_t;

#define PWM_MOTOR_ENABLE_PWM_CMD                    0x04
#define PWM_MOTOR_DISABLE_PWM_CMD                    0x08
#define PWM_MOTOR_CONFIG_PWM_CMD                    0x10

#ifdef __cplusplus
extern "C" {
#endif

static int pwm_motor_fb;

JNIEXPORT jint JNICALL
Java_com_samton_pwmmotor_PwmMotor_disable(JNIEnv *env, jobject instance, jint pwm_id) {
    pwmmotor_ctl_t *pwmmotor_ctl;
    pwmmotor_ctl = (pwmmotor_ctl_t *) malloc(sizeof(pwmmotor_ctl_t));
    LOGI("pwmmotor_disable %d.", pwm_id);
    pwmmotor_ctl->pwm_id = pwm_id;
    jint result = ioctl(pwm_motor_fb, PWM_MOTOR_DISABLE_PWM_CMD, (jlong) pwmmotor_ctl);
    return result;
}
JNIEXPORT jint JNICALL
Java_com_samton_pwmmotor_PwmMotor_enable(JNIEnv *env, jobject instance, jint pwm_id) {
    pwmmotor_ctl_t *pwmmotor_ctl;
    pwmmotor_ctl = (pwmmotor_ctl_t *) malloc(sizeof(pwmmotor_ctl_t));

    LOGI("pwmmotor_enable %d.", pwm_id);
    pwmmotor_ctl->pwm_id = pwm_id;
    jint result = ioctl(pwm_motor_fb, PWM_MOTOR_ENABLE_PWM_CMD, (jlong) pwmmotor_ctl);
    return result;
}
JNIEXPORT jboolean JNICALL
Java_com_samton_pwmmotor_PwmMotor_config(JNIEnv *env, jobject instance, jint pwm_id,
                                         jint pwm_duty_ns, jint pwm_period_ns) {
    pwmmotor_ctl_t *pwmmotor_ctl;
    pwmmotor_ctl = (pwmmotor_ctl_t *) malloc(sizeof(pwmmotor_ctl_t));
    LOGI("pwmmotor_disable %d.", pwm_id);
    pwmmotor_ctl->pwm_id = pwm_id;
    pwmmotor_ctl->duty_ns = pwm_duty_ns;
    pwmmotor_ctl->period_ns = pwm_period_ns;
    jint result = ioctl(pwm_motor_fb, PWM_MOTOR_CONFIG_PWM_CMD, (jlong) pwmmotor_ctl);
    return (jboolean) result;
}
JNIEXPORT void JNICALL
Java_com_samton_pwmmotor_PwmMotor_close(JNIEnv *env, jobject instance) {
    close(pwm_motor_fb);
}
JNIEXPORT jboolean JNICALL
Java_com_samton_pwmmotor_PwmMotor_open(JNIEnv *env, jobject instance) {
    pwm_motor_fb = open("/dev/pwm_motor", O_RDWR);
    if (-1 == pwm_motor_fb) {
        LOGD("Open /dev/pwm_motor fail......\n");
        return (jboolean) pwm_motor_fb;
    }
    LOGD("new pwm_motor_fb: %d \n", pwm_motor_fb);

    return (jboolean) pwm_motor_fb;
}
#ifdef __cplusplus
}
#endif
