<template>
  <div class="baidu-calendar">
    <div class="calendar-header">
      <div class="header-left">
        <a-select :style="{ width: '80px' }" size="small" v-model:value="holidayFilter">
          <a-select-option value="all">假期</a-select-option>
          <a-select-option value="holiday">仅节假日</a-select-option>
          <a-select-option value="workday">仅工作日</a-select-option>
        </a-select>
      </div>
      <div class="header-center">
        <a-select :style="{ width: '80px' }" size="small" v-model:value="currentYear">
          <a-select-option v-for="year in yearOptions" :key="year" :value="year">{{ year }}年</a-select-option>
        </a-select>
        <a-button type="text" size="small" @click="prevMonth">
          <LeftOutlined />
        </a-button>
        <a-select :style="{ width: '70px' }" size="small" v-model:value="currentMonth">
          <a-select-option v-for="month in monthOptions" :key="month.value" :value="month.value">{{ month.label }}</a-select-option>
        </a-select>
        <a-button type="text" size="small" @click="nextMonth">
          <RightOutlined />
        </a-button>
      </div>
      <div class="header-right">
        <a-button size="small" @click="goToday">今天</a-button>
      </div>
    </div>

    <div class="calendar-body">
      <div class="weekdays">
        <div class="weekday" v-for="day in weekdays" :key="day" :class="{ weekend: day === '六' || day === '日' }">
          {{ day }}
        </div>
      </div>

      <div class="days-grid">
        <div
          v-for="(day, index) in calendarDays"
          :key="index"
          class="day-cell"
          :class="{
            'other-month': !day.isCurrentMonth,
            'today': day.isToday,
            'selected': day.isSelected,
            'holiday': day.isHoliday && !day.isWorkday,
            'workday-holiday': day.isWorkday,
            'weekend': day.dayOfWeek === 6 || day.dayOfWeek === 0,
            'has-mark': day.markText
          }"
          @click="selectDate(day)"
        >
          <span class="day-number">{{ day.date }}</span>
          <span class="day-lunar">{{ day.lunar }}</span>
          <span v-if="day.markText" class="day-mark">{{ day.markText }}</span>
        </div>
      </div>
    </div>

    <div v-if="selectedDateData" class="selected-date-info">
      <div class="info-header">
        <CalendarOutlined />
        <span>{{ selectedDateData.formattedDate }}</span>
        <span class="lunar-date">{{ selectedDateData.lunar }}</span>
      </div>
      <div v-if="selectedDateData.isHoliday && !selectedDateData.isWorkday" class="info-holiday">
        <span class="holiday-name">{{ selectedDateData.holidayName }}</span>
      </div>
      <div v-if="selectedDateData.isWorkday" class="info-workday">
        <span>调休上班</span>
      </div>
      <div v-if="selectedDateData.announcements.length > 0" class="info-announcements">
        <div class="announcement-title">当天公告</div>
        <ul>
          <li v-for="ann in selectedDateData.announcements" :key="ann.id">
            <span class="dot"></span>
            <span>{{ ann.title }}</span>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { CalendarOutlined, LeftOutlined, RightOutlined } from '@ant-design/icons-vue';
import dayjs from 'dayjs';

const props = defineProps({
  announcements: {
    type: Array,
    default: () => []
  }
});

const weekdays = ['一', '二', '三', '四', '五', '六', '日'];
const monthOptions = [
  { value: 1, label: '1月' },
  { value: 2, label: '2月' },
  { value: 3, label: '3月' },
  { value: 4, label: '4月' },
  { value: 5, label: '5月' },
  { value: 6, label: '6月' },
  { value: 7, label: '7月' },
  { value: 8, label: '8月' },
  { value: 9, label: '9月' },
  { value: 10, label: '10月' },
  { value: 11, label: '11月' },
  { value: 12, label: '12月' }
];

const today = dayjs();
const currentYear = ref(today.year());
const currentMonth = ref(today.month() + 1);
const selectedDate = ref(today);
const holidayFilter = ref('all');

const yearOptions = computed(() => {
  const current = today.year();
  const years = [];
  for (let i = 1900; i <= current + 5; i++) {
    years.push(i);
  }
  return years;
});

const holidays2026 = {
  '2026-01-01': { name: '元旦', isWorkday: false },
  '2026-01-02': { name: '元旦', isWorkday: false },
  '2026-01-03': { name: '元旦', isWorkday: false },
  '2026-02-16': { name: '除夕', isWorkday: false },
  '2026-02-17': { name: '春节', isWorkday: false },
  '2026-02-18': { name: '春节', isWorkday: false },
  '2026-02-19': { name: '春节', isWorkday: false },
  '2026-02-20': { name: '春节', isWorkday: false },
  '2026-02-21': { name: '春节', isWorkday: false },
  '2026-02-22': { name: '春节', isWorkday: false },
  '2026-02-14': { name: '调休', isWorkday: true },
  '2026-02-28': { name: '调休', isWorkday: true },
  '2026-04-04': { name: '清明', isWorkday: false },
  '2026-04-05': { name: '清明', isWorkday: false },
  '2026-04-06': { name: '清明', isWorkday: false },
  '2026-05-01': { name: '劳动节', isWorkday: false },
  '2026-05-02': { name: '劳动节', isWorkday: false },
  '2026-05-03': { name: '劳动节', isWorkday: false },
  '2026-05-04': { name: '劳动节', isWorkday: false },
  '2026-05-05': { name: '劳动节', isWorkday: false },
  '2026-04-26': { name: '调休', isWorkday: true },
  '2026-05-09': { name: '调休', isWorkday: true },
  '2026-06-25': { name: '端午', isWorkday: false },
  '2026-06-26': { name: '端午', isWorkday: false },
  '2026-06-27': { name: '端午', isWorkday: false },
  '2026-09-27': { name: '中秋', isWorkday: false },
  '2026-09-28': { name: '中秋', isWorkday: false },
  '2026-09-29': { name: '中秋', isWorkday: false },
  '2026-10-01': { name: '国庆', isWorkday: false },
  '2026-10-02': { name: '国庆', isWorkday: false },
  '2026-10-03': { name: '国庆', isWorkday: false },
  '2026-10-04': { name: '国庆', isWorkday: false },
  '2026-10-05': { name: '国庆', isWorkday: false },
  '2026-10-06': { name: '国庆', isWorkday: false },
  '2026-10-07': { name: '国庆', isWorkday: false },
  '2026-09-26': { name: '调休', isWorkday: true },
  '2026-10-10': { name: '调休', isWorkday: true }
};

const solarTerms = {
  '2026-01-05': '小寒',
  '2026-01-20': '大寒',
  '2026-02-04': '立春',
  '2026-02-19': '雨水',
  '2026-03-06': '惊蛰',
  '2026-03-21': '春分',
  '2026-04-05': '清明',
  '2026-04-20': '谷雨',
  '2026-05-06': '立夏',
  '2026-05-21': '小满',
  '2026-06-06': '芒种',
  '2026-06-21': '夏至',
  '2026-07-07': '小暑',
  '2026-07-23': '大暑',
  '2026-08-08': '立秋',
  '2026-08-23': '处暑',
  '2026-09-08': '白露',
  '2026-09-23': '秋分',
  '2026-10-08': '寒露',
  '2026-10-24': '霜降',
  '2026-11-08': '立冬',
  '2026-11-23': '小雪',
  '2026-12-07': '大雪',
  '2026-12-22': '冬至'
};

const festivals = {
  '01-01': '元旦',
  '02-14': '情人节',
  '03-08': '妇女节',
  '03-12': '植树节',
  '04-01': '愚人节',
  '05-01': '劳动节',
  '05-04': '青年节',
  '06-01': '儿童节',
  '07-01': '建党节',
  '08-01': '建军节',
  '09-10': '教师节',
  '10-01': '国庆节',
  '11-11': '光棍节',
  '12-25': '圣诞节'
};

const lunarDays = ['初一', '初二', '初三', '初四', '初五', '初六', '初七', '初八', '初九', '初十',
  '十一', '十二', '十三', '十四', '十五', '十六', '十七', '十八', '十九', '二十',
  '廿一', '廿二', '廿三', '廿四', '廿五', '廿六', '廿七', '廿八', '廿九', '三十'];

const getLunarDay = (date) => {
  const baseDate = dayjs('1900-01-31');
  const daysDiff = date.diff(baseDate, 'day');
  const lunarIndex = (daysDiff % 30 + 30) % 30;
  return lunarDays[lunarIndex];
};

const getMarkText = (dateStr, date) => {
  if (holidays2026[dateStr] && !holidays2026[dateStr].isWorkday) {
    return holidays2026[dateStr].name;
  }
  if (solarTerms[dateStr]) {
    return solarTerms[dateStr];
  }
  const monthDay = dateStr.substring(5);
  if (festivals[monthDay]) {
    return festivals[monthDay];
  }
  return '';
};

const calendarDays = computed(() => {
  const days = [];
  const firstDayOfMonth = dayjs(`${currentYear.value}-${currentMonth.value}-01`);
  const lastDayOfMonth = firstDayOfMonth.endOf('month');
  const startDayOfWeek = firstDayOfMonth.day();
  const adjustedStartDay = startDayOfWeek === 0 ? 6 : startDayOfWeek - 1;
  const prevMonthLastDay = firstDayOfMonth.subtract(1, 'month').endOf('month').date();

  for (let i = adjustedStartDay - 1; i >= 0; i--) {
    const date = prevMonthLastDay - i;
    const month = currentMonth.value - 1 || 12;
    const year = month === 12 ? currentYear.value - 1 : currentYear.value;
    const fullDate = dayjs(`${year}-${month}-${date}`);
    days.push(createDayData(fullDate, false));
  }

  for (let i = 1; i <= lastDayOfMonth.date(); i++) {
    const fullDate = dayjs(`${currentYear.value}-${currentMonth.value}-${i}`);
    days.push(createDayData(fullDate, true));
  }

  const remainingDays = 42 - days.length;
  for (let i = 1; i <= remainingDays; i++) {
    const month = currentMonth.value + 1 > 12 ? 1 : currentMonth.value + 1;
    const year = month === 1 ? currentYear.value + 1 : currentYear.value;
    const fullDate = dayjs(`${year}-${month}-${i}`);
    days.push(createDayData(fullDate, false));
  }

  return days;
});

const createDayData = (fullDate, isCurrentMonth) => {
  const dateStr = fullDate.format('YYYY-MM-DD');
  const holidayInfo = holidays2026[dateStr];
  const announcementsOnDate = props.announcements.filter(ann => {
    if (!ann.createTime) return false;
    return dayjs(ann.createTime).format('YYYY-MM-DD') === dateStr;
  });
  const dayOfWeek = fullDate.day();

  return {
    date: fullDate.date(),
    month: fullDate.month() + 1,
    year: fullDate.year(),
    fullDate,
    dayOfWeek,
    isCurrentMonth,
    isToday: fullDate.isSame(today, 'day'),
    isSelected: fullDate.isSame(selectedDate.value, 'day'),
    isHoliday: holidayInfo && !holidayInfo.isWorkday,
    isWorkday: holidayInfo && holidayInfo.isWorkday,
    holidayName: holidayInfo?.name || '',
    lunar: getLunarDay(fullDate),
    markText: getMarkText(dateStr, fullDate),
    hasAnnouncement: announcementsOnDate.length > 0,
    announcements: announcementsOnDate
  };
};

const selectedDateData = computed(() => {
  const dateStr = selectedDate.value.format('YYYY-MM-DD');
  const holidayInfo = holidays2026[dateStr];
  const announcementsOnDate = props.announcements.filter(ann => {
    if (!ann.createTime) return false;
    return dayjs(ann.createTime).format('YYYY-MM-DD') === dateStr;
  });

  return {
    formattedDate: selectedDate.value.format('YYYY年MM月DD日'),
    lunar: getLunarDay(selectedDate.value),
    isHoliday: holidayInfo && !holidayInfo.isWorkday,
    isWorkday: holidayInfo && holidayInfo.isWorkday,
    holidayName: holidayInfo?.name || '',
    announcements: announcementsOnDate
  };
});

const prevMonth = () => {
  if (currentMonth.value === 1) {
    currentMonth.value = 12;
    currentYear.value--;
  } else {
    currentMonth.value--;
  }
};

const nextMonth = () => {
  if (currentMonth.value === 12) {
    currentMonth.value = 1;
    currentYear.value++;
  } else {
    currentMonth.value++;
  }
};

const goToday = () => {
  currentYear.value = today.year();
  currentMonth.value = today.month() + 1;
  selectedDate.value = today;
};

const selectDate = (day) => {
  selectedDate.value = day.fullDate;
};
</script>

<style scoped>
.baidu-calendar {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.calendar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-bottom: 1px solid #e2e8f0;
}

.calendar-header .ant-select-selector {
  border-radius: 6px !important;
}

.header-left {
  display: flex;
  align-items: center;
}

.header-center {
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-center .ant-btn {
  padding: 4px 8px;
}

.header-right {
  display: flex;
  align-items: center;
}

.header-right .ant-btn {
  border-radius: 6px;
  font-size: 13px;
}

.calendar-body {
  padding: 16px;
}

.weekdays {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  margin-bottom: 8px;
}

.weekday {
  text-align: center;
  font-size: 13px;
  font-weight: 500;
  color: #64748b;
  padding: 8px 0;
}

.weekday.weekend {
  color: #ef4444;
}

.days-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 2px;
}

.day-cell {
  max-width: 55px;
  max-height: 55px;
  aspect-ratio: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 2px;
  border-radius: 5px;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
}

.day-cell:hover {
  background: #f1f5f9;
}

.day-cell.other-month {
  opacity: 0.35;
}

.day-cell.other-month .day-number,
.day-cell.other-month .day-lunar {
  color: #94a3b8;
}

.day-cell.today {
  border: 1.5px solid #3b82f6;
}

.day-cell.today .day-number {
  color: #3b82f6;
  font-weight: 600;
}

.day-cell.selected {
  background: #f1f5f9;
}

.day-cell.holiday:not(.today) .day-number {
  color: #ef4444;
}

.day-cell.workday-holiday:not(.today) {
  background: #fef3c7;
}

.day-cell.workday-holiday:not(.today) .day-number {
  color: #d97706;
}

.day-cell.weekend:not(.holiday) .day-number {
  color: #ef4444;
}

.day-number {
  font-size: 16px;
  font-weight: 500;
  color: #1e293b;
  line-height: 1.2;
  text-align: center;
}

.day-lunar {
  font-size: 11px;
  color: #94a3b8;
  margin-top: 1px;
  text-align: center;
}

.day-mark {
  font-size: 9px;
  color: #ef4444;
  margin-top: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 90%;
  text-align: center;
}

.selected-date-info {
  padding: 12px 16px;
  background: #f8fafc;
  border-top: 1px solid #e2e8f0;
}

.info-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 8px;
}

.info-header .anticon {
  color: #3b82f6;
}

.lunar-date {
  font-size: 12px;
  color: #64748b;
  font-weight: 400;
}

.info-holiday {
  display: inline-block;
  padding: 4px 10px;
  background: #fee2e2;
  border-radius: 4px;
  font-size: 12px;
  color: #ef4444;
  margin-bottom: 8px;
}

.info-workday {
  display: inline-block;
  padding: 4px 10px;
  background: #fef3c7;
  border-radius: 4px;
  font-size: 12px;
  color: #d97706;
  margin-bottom: 8px;
}

.info-announcements {
  padding: 8px 0;
}

.announcement-title {
  font-size: 12px;
  font-weight: 500;
  color: #3b82f6;
  margin-bottom: 6px;
}

.info-announcements ul {
  margin: 0;
  padding: 0;
  list-style: none;
}

.info-announcements li {
  display: flex;
  align-items: flex-start;
  gap: 6px;
  padding: 2px 0;
  font-size: 12px;
  color: #64748b;
}

.info-announcements li .dot {
  width: 4px;
  height: 4px;
  background: #3b82f6;
  border-radius: 50%;
  margin-top: 5px;
  flex-shrink: 0;
}
</style>