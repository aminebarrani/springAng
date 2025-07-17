import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

interface Personne {
  idPersonne: number;
  nom: string;
  prenom: string;
}

interface Todo {
  idTache: number;
  descrip: string;
  datebeb: string;
  datefin: string;
  isChecked: boolean;
  personne: Personne;
}

interface CalendarDay {
  date: Date;
  todos: Todo[];
  isToday: boolean;
  isCurrentMonth: boolean;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit {
  currentDate = new Date();
  calendarDays: CalendarDay[] = [];
  todos: Todo[] = [];
  isLoading = false;
  errorMessage: string | null = null;
  selectedDate: Date | null = null;
  selectedTodos: Todo[] = [];

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit() {
    this.generateCalendar();
    this.fetchAllTodos();
  }

  generateCalendar() {
    const year = this.currentDate.getFullYear();
    const month = this.currentDate.getMonth();
    const firstDay = new Date(year, month, 1);
    const lastDay = new Date(year, month + 1, 0);
    const startDate = new Date(firstDay);
    startDate.setDate(startDate.getDate() - firstDay.getDay());
    const endDate = new Date(lastDay);
    endDate.setDate(endDate.getDate() + (6 - lastDay.getDay()));
    this.calendarDays = [];
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    for (let d = new Date(startDate); d <= endDate; d.setDate(d.getDate() + 1)) {
      const date = new Date(d);
      const dayTodos = this.getTodosForDate(date);
      this.calendarDays.push({
        date: new Date(date),
        todos: dayTodos,
        isToday: date.getTime() === today.getTime(),
        isCurrentMonth: date.getMonth() === month
      });
    }
  }

  getTodosForDate(date: Date): Todo[] {
    return this.todos.filter(todo => {
      const startDate = todo.datebeb ? new Date(todo.datebeb) : null;
      const endDate = todo.datefin ? new Date(todo.datefin) : null;
      if (!startDate && !endDate) return false;
      const checkDate = new Date(date);
      checkDate.setHours(0, 0, 0, 0);
      if (startDate && endDate) {
        startDate.setHours(0, 0, 0, 0);
        endDate.setHours(0, 0, 0, 0);
        return checkDate >= startDate && checkDate <= endDate;
      } else if (startDate) {
        startDate.setHours(0, 0, 0, 0);
        return checkDate.getTime() === startDate.getTime();
      } else if (endDate) {
        endDate.setHours(0, 0, 0, 0);
        return checkDate.getTime() === endDate.getTime();
      }
      return false;
    });
  }

  fetchAllTodos() {
    this.isLoading = true;
    this.errorMessage = null;
    const token = localStorage.getItem('auth_token');
    if (!token) {
      this.handleAuthError();
      return;
    }
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });
    this.http.get<Todo[]>('/api/todolist', { headers }).subscribe({
      next: (todos) => {
        console.log('Fetched todos:', todos); // Debug log to see the data structure
        this.todos = todos;
        this.generateCalendar();
        this.isLoading = false;
      },
      error: (err) => {
        this.handleError('Error fetching todos:', err, 'Failed to load todos!');
      }
    });
  }

  previousMonth() {
    this.currentDate.setMonth(this.currentDate.getMonth() - 1);
    this.generateCalendar();
  }

  nextMonth() {
    this.currentDate.setMonth(this.currentDate.getMonth() + 1);
    this.generateCalendar();
  }

  selectDate(day: CalendarDay) {
    this.selectedDate = day.date;
    this.selectedTodos = day.todos;
  }

  goToTodoList(personneId: number) {
    this.router.navigate(['/todo-crud', personneId]);
  }

  logout() {
    localStorage.removeItem('auth_token');
    this.router.navigate(['/']);
  }

  goToAdminPanel() {
    this.router.navigate(['/admin-panel']);
  }

  private handleAuthError() {
    this.isLoading = false;
    this.errorMessage = 'No auth token found';
    alert('You are not logged in or token is missing!');
    this.router.navigate(['/']);
  }

  private handleError(logMessage: string, err: any, alertMessage: string) {
    console.error(logMessage, err);
    this.isLoading = false;
    this.errorMessage = alertMessage;
    alert(alertMessage);
  }

  get completedTodosCount(): number {
    return this.todos.filter(t => t.isChecked).length;
  }

  get pendingTodosCount(): number {
    return this.todos.filter(t => !t.isChecked).length;
  }
} 