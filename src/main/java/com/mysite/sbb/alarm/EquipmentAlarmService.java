package com.mysite.sbb.alarm;
// 날짜와 시간을 사용하기 위한 import
import java.time.LocalDateTime;
// List 사용을 위한 import
import java.util.List;
// Spring Service import
import org.springframework.stereotype.Service;

// Service 클래스 선언
@Service
public class EquipmentAlarmService {
   // Repository 객체 선언
   private final EquipmentAlarmRepository equipmentAlarmRepository;
   // 생성자 주입
   public EquipmentAlarmService(EquipmentAlarmRepository equipmentAlarmRepository) {

      this.equipmentAlarmRepository = equipmentAlarmRepository;
   }
   // 전체 알람 조회
   public List<EquipmentAlarm> getList() {

      return this.equipmentAlarmRepository.findAll();
   }
   // 특정 알람 조회
   public EquipmentAlarm getAlarm(Integer id) {
      return this.equipmentAlarmRepository.findById(id)
            // 데이터가 없으면 오류 발생
            .orElseThrow(() -> new RuntimeException("알람 데이터를 찾을 수 없습니다."));
   }
   // 수동 알람 생성
   public EquipmentAlarm createManual(String equipmentName, String alarmCode, String alarmLevel, String message) {
      return saveAlarm(equipmentName, alarmCode, alarmLevel, message, null);
   }
   // AI 자동 알람 생성
   public EquipmentAlarm createAuto(String equipmentName, String message, Integer predictionResultId) {
      // 현재 시간을 이용한 알람 코드 생성
      String alarmCode = "AI-" + System.currentTimeMillis();
      return saveAlarm(equipmentName, alarmCode, "CRITICAL", message, predictionResultId);
   }
   // 알람 저장 공통 메서드
   private EquipmentAlarm saveAlarm(String equipmentName, String alarmCode, String alarmLevel, String message,
         Integer predictionResultId) {
      // EquipmentAlarm 객체 생성
      EquipmentAlarm alarm = new EquipmentAlarm();
      // 데이터 저장
      alarm.setEquipmentName(equipmentName);
      alarm.setAlarmCode(alarmCode);
      alarm.setAlarmLevel(alarmLevel);
      alarm.setMessage(message);
      alarm.setPredictionResultId(predictionResultId);

      // 현재 시간 저장
      alarm.setCreateDate(LocalDateTime.now());
      // DB 저장
      return this.equipmentAlarmRepository.save(alarm);
   }

   // 조치 내용 저장
   public void saveAction(Integer id, String actionMemo) {
      // 알람 조회
      EquipmentAlarm alarm = getAlarm(id);
      // 조치 내용 저장
      alarm.setActionMemo(actionMemo);
      // 조치 완료 시간 저장
      alarm.setActionDate(LocalDateTime.now());
      // DB 저장
      this.equipmentAlarmRepository.save(alarm);
   }
}