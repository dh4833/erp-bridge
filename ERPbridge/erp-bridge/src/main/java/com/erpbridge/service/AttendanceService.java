package com.erpbridge.service;

import com.erpbridge.entity.Attendance;
import com.erpbridge.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttendanceService {

	private final AttendanceRepository attendanceRepository;

	// 출근 등록
	public String checkIn(String empId) {
		LocalDate today = LocalDate.now();

		// 오늘 이미 출근했는지 확인
		Optional<Attendance> existing = attendanceRepository.findByEmpIdAndAttDate(empId, today);

		if (existing.isPresent()) {
			return "ALREADY_CHECKED_IN";
		}

		LocalDateTime now = LocalDateTime.now();

		// 9시 이후면 지각 처리
		LocalTime nineAM = LocalTime.of(9, 0);
		String attType = now.toLocalTime().isAfter(nineAM) ? "지각" : "정상";

		Attendance att = Attendance.builder().empId(empId).attDate(today).checkIn(now).attType(attType).status("미확정")
				.build();

		attendanceRepository.save(att);
		return "SUCCESS";
	}

	// 퇴근 등록
	public String checkOut(String empId) {
		LocalDate today = LocalDate.now();

		Optional<Attendance> existing = attendanceRepository.findByEmpIdAndAttDate(empId, today);

		if (existing.isEmpty()) {
			return "NOT_CHECKED_IN";
		}

		Attendance att = existing.get();

		if (att.getCheckOut() != null) {
			return "ALREADY_CHECKED_OUT";
		}

		att.setCheckOut(LocalDateTime.now());
		attendanceRepository.save(att);
		return "SUCCESS";
	}

	// 내 근태 내역 조회
	public List<Attendance> getMyAttendance(String empId) {
		return attendanceRepository.findByEmpIdOrderByAttDateDesc(empId);
	}

	// 오늘 내 근태 조회
	public Attendance getTodayAttendance(String empId) {
		return attendanceRepository.findByEmpIdAndAttDate(empId, LocalDate.now()).orElse(null);
	}

	// 특정 월 근태 조회
	public List<Attendance> getMonthlyAttendance(String empId, int year, int month) {
		LocalDate start = LocalDate.of(year, month, 1);
		LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
		return attendanceRepository.findByEmpIdAndAttDateBetween(empId, start, end);
	}

	// 오늘 전체 직원 근태 조회
	public List<Attendance> getTodayAllAttendance() {
		return attendanceRepository.findByAttDate(LocalDate.now());
	}
}