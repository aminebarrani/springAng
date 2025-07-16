import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-todo-crud',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './todo-crud.html',
  styleUrl: './todo-crud.css'
})
export class TodoCrud implements OnInit {
  todos: any[] = [];
  personneId: number | null = null;
  personneName: string = '';
  newTodo: string = '';
  newDatebeb: string = '';
  newDatefin: string = '';
  newIsChecked: boolean = false;
  isLoading: boolean = false;
  errorMessage: string | null = null;

  constructor(private http: HttpClient, private route: ActivatedRoute) {}

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const id = params.get('personneId');
      this.personneId = id ? Number(id) : null;
      if (this.personneId) {
        this.fetchPersonne();
        this.fetchTodos();
      }
    });
  }

  fetchPersonne() {
    if (!this.personneId) return;

    this.isLoading = true;
    this.errorMessage = null;

    const token = localStorage.getItem('auth_token');
    if (!token) {
      this.handleAuthError();
      return;
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    this.http.get(`/api/personnes/${this.personneId}`, { headers }).subscribe({
      next: (personne: any) => {
        this.personneName = `${personne.nom} ${personne.prenom}`;
        this.isLoading = false;
      },
      error: (err) => {
        this.handleError('Error fetching person:', err, 'Personne not found or access denied!');
      }
    });
  }

  fetchTodos() {
    if (!this.personneId) return;

    this.isLoading = true;
    this.errorMessage = null;

    const token = localStorage.getItem('auth_token');
    if (!token) {
      this.handleAuthError();
      return;
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    this.http.get(`/api/todolist/personne/${this.personneId}`, { headers }).subscribe({
      next: (todos: any) => {
        this.todos = todos.map((todo: any) => ({
          ...todo,
          editing: false,
          editDesc: todo.descrip,
          editDatebeb: todo.datebeb,
          editDatefin: todo.datefin
        }));
        this.isLoading = false;
      },
      error: (err) => {
        this.handleError('Error fetching todos:', err, 'Failed to load todos!');
      }
    });
  }

  addTodo() {
    if (!this.newTodo.trim() || !this.personneId) return;

    this.isLoading = true;
    this.errorMessage = null;

    const token = localStorage.getItem('auth_token');
    if (!token) {
      this.handleAuthError();
      return;
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    this.http.post('/api/todolist', {
      descrip: this.newTodo,
      personne: { idPersonne: this.personneId },
      datebeb: this.newDatebeb || null,
      datefin: this.newDatefin || null,
      isChecked: this.newIsChecked
    }, { headers }).subscribe({
      next: () => {
        this.resetForm();
        this.fetchTodos();
      },
      error: (err) => {
        this.handleError('Error adding todo:', err, 'Failed to add todo!');
      }
    });
  }

  updateTodo(todo: any) {
    if (!this.personneId || !todo?.idTache) return;

    this.isLoading = true;
    this.errorMessage = null;

    const token = localStorage.getItem('auth_token');
    if (!token) {
      this.handleAuthError();
      return;
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    this.http.put(`/api/todolist/${todo.idTache}`, {
      ...todo,
      descrip: todo.editDesc,
      personne: { idPersonne: this.personneId },
      datebeb: todo.editDatebeb || null,
      datefin: todo.editDatefin || null,
      isChecked: todo.isChecked
    }, { headers }).subscribe({
      next: () => {
        todo.editing = false;
        this.fetchTodos();
      },
      error: (err) => {
        this.handleError('Error updating todo:', err, 'Failed to update todo!');
      }
    });
  }

  toggleTodoCheck(todo: any) {
    if (!todo?.idTache) return;
    
    const originalState = todo.isChecked;
    todo.isChecked = !todo.isChecked;
    
    this.isLoading = true;
    this.errorMessage = null;

    const token = localStorage.getItem('auth_token');
    if (!token) {
      this.handleAuthError();
      todo.isChecked = originalState;
      return;
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    this.http.put(`/api/todolist/${todo.idTache}`, {
      ...todo,
      isChecked: todo.isChecked
    }, { headers }).subscribe({
      next: () => {
        this.isLoading = false;
      },
      error: (err) => {
        this.handleError('Error toggling todo:', err, 'Failed to toggle todo status!');
        todo.isChecked = originalState;
      }
    });
  }

  deleteTodo(todo: any) {
    if (!todo?.idTache) return;

    this.isLoading = true;
    this.errorMessage = null;

    const token = localStorage.getItem('auth_token');
    if (!token) {
      this.handleAuthError();
      return;
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    this.http.delete(`/api/todolist/${todo.idTache}`, { headers }).subscribe({
      next: () => {
        this.fetchTodos();
      },
      error: (err) => {
        this.handleError('Error deleting todo:', err, 'Failed to delete todo!');
      }
    });
  }

  private resetForm() {
    this.newTodo = '';
    this.newDatebeb = '';
    this.newDatefin = '';
    this.newIsChecked = false;
    this.isLoading = false;
  }

  private handleAuthError() {
    this.isLoading = false;
    this.errorMessage = 'No auth token found';
    alert('You are not logged in or token is missing!');
  }

  private handleError(logMessage: string, err: any, alertMessage: string) {
    console.error(logMessage, err);
    this.isLoading = false;
    this.errorMessage = alertMessage;
    alert(alertMessage);
  }
}