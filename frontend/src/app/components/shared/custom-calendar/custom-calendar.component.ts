import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  Output,
} from '@angular/core';

@Component({
  selector: 'app-custom-calendar',
  templateUrl: './custom-calendar.component.html',
  styleUrls: ['./custom-calendar.component.scss'],
})
export class CustomCalendarComponent implements OnChanges {
  currentMonth: number = new Date().getMonth();
  currentYear: number = new Date().getFullYear();

  // TODO: Change the type of the plannedItemList to match the actual type
  @Input() plannedItemList: Map<number, object[]> = new Map<number, object[]>();

  @Output() monthChanged = new EventEmitter<{ year: number; month: number }>();

  ngOnChanges(): void {
    if (this.plannedItemList === undefined) {
      return;
    }
  }

  getMonthName(): string {
    return new Date(this.currentYear, this.currentMonth).toLocaleString(
      'en-US',
      { month: 'long' }
    );
  }

  nextMonth(): void {
    if (this.currentMonth === 11) {
      this.currentMonth = 0;
      this.currentYear++;
    } else {
      this.currentMonth++;
    }

    this.monthChanged.emit({
      year: this.currentYear,
      month: this.currentMonth,
    });
  }

  previousMonth(): void {
    if (this.currentMonth === 0) {
      this.currentMonth = 11;
      this.currentYear--;
    } else {
      this.currentMonth--;
    }

    this.monthChanged.emit({
      year: this.currentYear,
      month: this.currentMonth,
    });
  }

  currentMonthAndYear(): void {
    this.currentMonth = new Date().getMonth();
    this.currentYear = new Date().getFullYear();

    this.monthChanged.emit({
      year: this.currentYear,
      month: this.currentMonth,
    });
  }

  getDaysInMonth(): number {
    return new Date(this.currentYear, this.currentMonth + 1, 0).getDate();
  }

  getDayNumberForPosition(week: number, day: number): number | string {
    const firstDayOfMonth =
      (new Date(this.currentYear, this.currentMonth).getDay() + 6) % 7;
    const dayNumber = week * 7 + day - firstDayOfMonth + 1;

    if (dayNumber <= 0 || dayNumber > this.getDaysInMonth()) {
      return '';
    }
    return dayNumber.toString();
  }

  getTodayIndicator(week: number, day: number): boolean {
    const dayNumber = this.getDayNumberForPosition(week, day);

    if (dayNumber === '') {
      return false;
    }

    return (
      dayNumber.toString() === new Date().getDate().toString() &&
      this.currentMonth === new Date().getMonth() &&
      this.currentYear === new Date().getFullYear()
    );
  }

  getWeeksForDisplayInMonth(): number[] {
    const firstDayOfMonth =
      (new Date(this.currentYear, this.currentMonth).getDay() + 6) % 7;
    const daysInMonth = this.getDaysInMonth();

    return Array(Math.ceil((daysInMonth + firstDayOfMonth) / 7))
      .fill(0)
      .map((x, i) => i);
  }

  getItemsForDay(week: number, day: number): any {
    const dayNumber = this.getDayNumberForPosition(week, day);
    if (dayNumber === '') {
      return [];
    }

    const dayNumberString = dayNumber.toString();

    return this.plannedItemList?.get(Number(dayNumberString));
  }
}
