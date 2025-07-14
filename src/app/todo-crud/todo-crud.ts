import { Component, OnInit, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpHeaders } from '@angular/common/http';


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
  isLoading: boolean = false;
  errorMessage: string | null = null;

  constructor(private http: HttpClient, private route: ActivatedRoute) {}

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const id = params.get('personneId'); // Ensure this matches your route
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

  // ✅ Get the token from localStorage (make sure you store it as 'auth_token')
  const token = localStorage.getItem('auth_token');

  if (!token) {
    this.isLoading = false;
    this.errorMessage = 'No auth token found';
    alert('You are not logged in or token is missing!');
    return;
  }

  const headers = new HttpHeaders({
    'Authorization': `Bearer ${token}`
  });

  this.http.get(`/api/personnes/${this.personneId}`, { headers }).subscribe({
    next: (personne: any) => {
      this.personneName = personne.nom + ' ' + personne.prenom;
      this.isLoading = false;
    },
    error: (err) => {
      console.error('Error fetching person:', err);
      this.personneName = '';
      this.isLoading = false;
      this.errorMessage = 'Failed to load person details';
      alert('Personne not found or access denied!');
    }
  });
}


  
fetchTodos() {
  if (!this.personneId) return;

  this.isLoading = true;
  this.errorMessage = null;

  // ✅ Get token from localStorage
  const token = localStorage.getItem('auth_token');

  if (!token) {
    this.isLoading = false;
    this.errorMessage = 'No auth token found';
    alert('You are not logged in or token is missing!');
    return;
  }

  // ✅ Add Authorization header
  const headers = new HttpHeaders({
    'Authorization': `Bearer ${token}`
  });

  // ✅ Same endpoint, but with header
  this.http.get(`/api/todolist/personne/${this.personneId}`, { headers }).subscribe({
    next: (todos: any) => {
      this.todos = todos;
      this.isLoading = false;
    },
    error: (err) => {
      console.error('Error fetching todos:', err);
      this.todos = [];
      this.isLoading = false;
      this.errorMessage = 'Failed to load todos';
      alert('Failed to load todos!');
    }
  });
}

  addTodo() {
    if (!this.newTodo.trim() || !this.personneId) return;

    this.isLoading = true;
    this.errorMessage = null;

    this.http.post('/api/todolist', {
      descrip: this.newTodo,
      personne: { idPersonne: this.personneId }
    }).subscribe({
      next: () => {
        this.newTodo = '';
        this.fetchTodos();
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error adding todo:', err);
        this.isLoading = false;
        this.errorMessage = 'Failed to add todo';
        alert('Failed to add todo!');
      }
    });
  }

  updateTodo(todo: any, newDesc: string) {
    if (!this.personneId || !todo?.idTache) return;

    this.isLoading = true;
    this.errorMessage = null;

    this.http.put(`/api/todolist/${todo.idTache}`, {
      ...todo,
      descrip: newDesc,
      personne: { idPersonne: this.personneId }
    }).subscribe({
      next: () => {
        this.fetchTodos();
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error updating todo:', err);
        this.isLoading = false;
        this.errorMessage = 'Failed to update todo';
        alert('Failed to update todo!');
      }
    });
  }

  deleteTodo(todo: any) {
    if (!todo?.idTache) return;

    this.isLoading = true;
    this.errorMessage = null;

    this.http.delete(`/api/todolist/${todo.idTache}`).subscribe({
      next: () => {
        this.fetchTodos();
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error deleting todo:', err);
        this.isLoading = false;
        this.errorMessage = 'Failed to delete todo';
        alert('Failed to delete todo!');
      }
    });
  }
}
