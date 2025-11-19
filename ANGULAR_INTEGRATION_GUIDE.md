# Angular Integration Guide for PERSONNE Role

## Overview

This guide explains what changes need to be made on the Angular side to work with the new PERSONNE role functionality in the backend.

## Key Changes Required

### 1. **User Registration Interface**

Update your registration form to include the new `personneId` field:

```typescript
// user.model.ts or auth.model.ts
export interface RegisterRequest {
  username: string;
  password: string;
  email: string;
  role: 'USER' | 'ADMIN' | 'PERSONNE'; // Add PERSONNE to role options
  phoneNumber?: string;
  firstname?: string;
  lastname?: string;
  image?: string;
  personneId?: number; // NEW FIELD
}

export interface User {
  id: number;
  username: string;
  email: string;
  role: string;
  phoneNumber: string;
  firstname: string;
  lastname: string;
  image: string;
  personne?: Personne | null; // FIXED: Allow null values from backend
}

export interface Personne {
  idPersonne: number;
  nom: string;
  prenom: string;
  adresse?: string;
  sex?: string;
  department: Department;
}
```

### 2. **Fix TypeScript Interface Mismatch**

**IMPORTANT**: The backend returns `personne: Personne | null`, but TypeScript interfaces often expect `personne?: Personne | undefined`. Here's how to fix this:

```typescript
// user.model.ts - Updated interface
export interface User {
  id: number;
  username: string;
  email: string;
  role: string;
  phoneNumber: string;
  firstname: string;
  lastname: string;
  image: string;
  personne?: Personne | null; // Allow both undefined and null
}

// Alternative approach - Create a type guard
export function hasPersonne(user: User): user is User & { personne: Personne } {
  return user.personne !== null && user.personne !== undefined;
}

// Usage in components
if (hasPersonne(currentUser)) {
  console.log('User has personne:', currentUser.personne.prenom);
}
```

### 3. **Update Auth Service to Handle Null Values**

```typescript
// auth.service.ts
export class AuthService {
  private apiUrl = 'http://localhost:8080/api';

  register(registerData: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/auth/register`, registerData);
  }

  login(loginData: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/auth/login`, loginData);
  }

  getCurrentUser(): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/auth/user`);
  }

  // NEW: Check if user has PERSONNE role
  isPersonne(): boolean {
    const user = this.getStoredUser();
    return user?.role === 'PERSONNE';
  }

  // NEW: Check if user has ADMIN role
  isAdmin(): boolean {
    const user = this.getStoredUser();
    return user?.role === 'ADMIN';
  }

  // NEW: Get current user's personne ID (handle null values)
  getCurrentPersonneId(): number | null {
    const user = this.getStoredUser();
    return user?.personne?.idPersonne || null;
  }

  // NEW: Check if user has a linked personne
  hasPersonne(): boolean {
    const user = this.getStoredUser();
    return user?.personne !== null && user?.personne !== undefined;
  }

  private getStoredUser(): User | null {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
  }

  // Updated storeUserData method to handle null personne
  storeUserData(token: string, user: User): void {
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify(user));
  }
}
```

### 4. **Fix Login Component**

Update your login component to handle the null personne values:

```typescript
// login.component.ts
export class LoginComponent {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onLogin(loginData: LoginRequest) {
    this.authService.login(loginData).subscribe({
      next: (response) => {
        // Handle the user data with potential null personne
        const userData: User = {
          id: response.user.id,
          username: response.user.username,
          email: response.user.email,
          role: response.user.role,
          firstname: response.user.firstname,
          lastname: response.user.lastname,
          phoneNumber: response.user.phoneNumber,
          image: response.user.image,
          personne: response.user.personne || null // Explicitly handle null
        };

        this.authService.storeUserData(response.token, userData);
        this.router.navigate(['/todos']);
      },
      error: (error) => {
        console.error('Login failed:', error);
        // Handle error
      }
    });
  }
}
```

### 5. **Update Components to Handle Null Personne**

```typescript
// todo-list.component.ts
export class TodoListComponent implements OnInit {
  todos: ToDoList[] = [];
  isAdmin = false;
  isPersonne = false;
  currentPersonneId: number | null = null;

  constructor(
    private todoService: TodoService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.isAdmin = this.authService.isAdmin();
    this.isPersonne = this.authService.isPersonne();
    this.currentPersonneId = this.authService.getCurrentPersonneId();
    
    this.loadTodos();
  }

  loadTodos() {
    if (this.isAdmin) {
      // Admin can see all todos
      this.todoService.getAllTodos().subscribe(todos => {
        this.todos = todos;
      });
    } else if (this.isPersonne && this.currentPersonneId) {
      // Personne can only see their own todos (if they have a personne ID)
      this.todoService.getMyTodos().subscribe(todos => {
        this.todos = todos;
      });
    } else {
      // Handle case where PERSONNE user doesn't have a linked personne
      console.warn('PERSONNE user without linked personne');
      this.todos = [];
    }
  }

  createTodo(todoData: any) {
    // For PERSONNE role, automatically set their personne ID
    if (this.isPersonne && this.currentPersonneId) {
      todoData.personne = { idPersonne: this.currentPersonneId };
    } else if (this.isPersonne) {
      // Handle case where PERSONNE user doesn't have a linked personne
      this.showError('PERSONNE user must have a linked personne to create todos');
      return;
    }

    this.todoService.createTodo(todoData).subscribe({
      next: (newTodo) => {
        this.todos.push(newTodo);
        this.showSuccess('Todo created successfully');
      },
      error: (error) => {
        this.showError(error.error || 'Failed to create todo');
      }
    });
  }

  // ... rest of the component methods
}
```

### 6. **Update Templates to Handle Null Values**

```html
<!-- todo-list.component.html -->
<div class="todo-container">
  <div class="todo-header">
    <h2>Todo List</h2>
    <div *ngIf="isAdmin" class="admin-info">
      <span class="badge badge-primary">Admin View - All Todos</span>
    </div>
    <div *ngIf="isPersonne" class="personne-info">
      <span class="badge badge-info">Personal Todos</span>
      <span *ngIf="!currentPersonneId" class="badge badge-warning">
        No linked personne
      </span>
    </div>
  </div>

  <!-- Show warning for PERSONNE users without linked personne -->
  <div *ngIf="isPersonne && !currentPersonneId" class="alert alert-warning">
    <strong>Warning:</strong> Your account is not linked to a personne. 
    Please contact an administrator to link your account.
  </div>

  <!-- Create Todo Form (only show if PERSONNE user has linked personne) -->
  <div class="create-todo-form" *ngIf="!isPersonne || currentPersonneId">
    <form (ngSubmit)="createTodo(todoForm.value)" #todoForm="ngForm">
      <!-- ... form fields ... -->
    </form>
  </div>

  <!-- Todo List -->
  <div class="todo-list">
    <div *ngFor="let todo of todos" class="todo-item">
      <div class="todo-content">
        <h4>{{ todo.descrip }}</h4>
        <p *ngIf="todo.datebeb">Start: {{ todo.datebeb | date }}</p>
        <p *ngIf="todo.datefin">End: {{ todo.datefin | date }}</p>
        <p>Status: 
          <span [class]="todo.isChecked ? 'badge badge-success' : 'badge badge-warning'">
            {{ todo.isChecked ? 'Completed' : 'Pending' }}
          </span>
        </p>
        
        <!-- Show personne info for admin (handle null values) -->
        <div *ngIf="isAdmin && todo.personne" class="personne-info">
          <small>Assigned to: {{ todo.personne.prenom }} {{ todo.personne.nom }}</small>
        </div>
      </div>

      <!-- ... action buttons ... -->
    </div>
  </div>

  <!-- Empty State -->
  <div *ngIf="todos.length === 0" class="empty-state">
    <p>No todos found.</p>
    <p *ngIf="isPersonne && currentPersonneId">Create your first todo to get started!</p>
    <p *ngIf="isPersonne && !currentPersonneId">
      You need to be linked to a personne to create todos.
    </p>
  </div>
</div>
```

### 7. **Registration Component Updates**

Update your registration component to handle the new fields:

```typescript
// register.component.ts
export class RegisterComponent {
  registerForm = this.fb.group({
    username: ['', Validators.required],
    password: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    role: ['USER', Validators.required],
    phoneNumber: [''],
    firstname: [''],
    lastname: [''],
    image: [''],
    personneId: [null] // NEW FIELD
  });

  // Add personne selection logic
  personnes: Personne[] = [];
  showPersonneSelection = false;

  ngOnInit() {
    this.loadPersonnes();
    this.registerForm.get('role')?.valueChanges.subscribe(role => {
      this.showPersonneSelection = role === 'PERSONNE';
      if (role !== 'PERSONNE') {
        this.registerForm.patchValue({ personneId: null });
      }
    });
  }

  loadPersonnes() {
    // Load available personnes for selection
    this.personneService.getPersonnes().subscribe(personnes => {
      this.personnes = personnes;
    });
  }

  onSubmit() {
    if (this.registerForm.valid) {
      const registerData = this.registerForm.value;
      
      // Ensure personneId is set for PERSONNE role
      if (registerData.role === 'PERSONNE' && !registerData.personneId) {
        this.showError('Personne selection is required for PERSONNE role');
        return;
      }

      this.authService.register(registerData).subscribe({
        next: (response) => {
          // Handle successful registration
          this.router.navigate(['/login']);
        },
        error: (error) => {
          this.showError(error.error.message || 'Registration failed');
        }
      });
    }
  }
}
```

### 8. **Registration Template Updates**

Update your registration template:

```html
<!-- register.component.html -->
<form [formGroup]="registerForm" (ngSubmit)="onSubmit()">
  <div class="form-group">
    <label for="username">Username</label>
    <input type="text" id="username" formControlName="username" class="form-control">
  </div>

  <div class="form-group">
    <label for="password">Password</label>
    <input type="password" id="password" formControlName="password" class="form-control">
  </div>

  <div class="form-group">
    <label for="email">Email</label>
    <input type="email" id="email" formControlName="email" class="form-control">
  </div>

  <div class="form-group">
    <label for="role">Role</label>
    <select id="role" formControlName="role" class="form-control">
      <option value="USER">User</option>
      <option value="ADMIN">Admin</option>
      <option value="PERSONNE">Personne</option>
    </select>
  </div>

  <!-- NEW: Personne Selection (only shown for PERSONNE role) -->
  <div class="form-group" *ngIf="showPersonneSelection">
    <label for="personneId">Select Personne</label>
    <select id="personneId" formControlName="personneId" class="form-control" required>
      <option value="">Choose a personne...</option>
      <option *ngFor="let personne of personnes" [value]="personne.idPersonne">
        {{ personne.prenom }} {{ personne.nom }} - {{ personne.department.nomDept }}
      </option>
    </select>
    <small class="form-text text-muted">
      Select the personne this user account will be linked to.
    </small>
  </div>

  <div class="form-group">
    <label for="phoneNumber">Phone Number</label>
    <input type="tel" id="phoneNumber" formControlName="phoneNumber" class="form-control">
  </div>

  <div class="form-group">
    <label for="firstname">First Name</label>
    <input type="text" id="firstname" formControlName="firstname" class="form-control">
  </div>

  <div class="form-group">
    <label for="lastname">Last Name</label>
    <input type="text" id="lastname" formControlName="lastname" class="form-control">
  </div>

  <button type="submit" [disabled]="!registerForm.valid" class="btn btn-primary">
    Register
  </button>
</form>
```

### 9. **Auth Service Updates**

Update your authentication service to handle the new registration data:

```typescript
// auth.service.ts
export class AuthService {
  private apiUrl = 'http://localhost:8080/api';

  register(registerData: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/auth/register`, registerData);
  }

  login(loginData: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/auth/login`, loginData);
  }

  getCurrentUser(): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/auth/user`);
  }

  // NEW: Check if user has PERSONNE role
  isPersonne(): boolean {
    const user = this.getStoredUser();
    return user?.role === 'PERSONNE';
  }

  // NEW: Check if user has ADMIN role
  isAdmin(): boolean {
    const user = this.getStoredUser();
    return user?.role === 'ADMIN';
  }

  // NEW: Get current user's personne ID (handle null values)
  getCurrentPersonneId(): number | null {
    const user = this.getStoredUser();
    return user?.personne?.idPersonne || null;
  }

  // NEW: Check if user has a linked personne
  hasPersonne(): boolean {
    const user = this.getStoredUser();
    return user?.personne !== null && user?.personne !== undefined;
  }

  private getStoredUser(): User | null {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
  }

  // Updated storeUserData method to handle null personne
  storeUserData(token: string, user: User): void {
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify(user));
  }
}
```

### 10. **Todo Service Updates**

Update your todo service to use the new endpoints:

```typescript
// todo.service.ts
export class TodoService {
  private apiUrl = 'http://localhost:8080/api';

  // NEW: Get current user's todos
  getMyTodos(): Observable<ToDoList[]> {
    return this.http.get<ToDoList[]>(`${this.apiUrl}/todolist/my-todos`);
  }

  // Get all todos (Admin only)
  getAllTodos(): Observable<ToDoList[]> {
    return this.http.get<ToDoList[]>(`${this.apiUrl}/todolist`);
  }

  // Get todos by personne ID (Admin only)
  getTodosByPersonne(personneId: number): Observable<ToDoList[]> {
    return this.http.get<ToDoList[]>(`${this.apiUrl}/todolist/personne/${personneId}`);
  }

  // Get specific todo
  getTodo(id: number): Observable<ToDoList> {
    return this.http.get<ToDoList>(`${this.apiUrl}/todolist/${id}`);
  }

  // Create todo
  createTodo(todo: ToDoList): Observable<ToDoList> {
    return this.http.post<ToDoList>(`${this.apiUrl}/todolist`, todo);
  }

  // Update todo
  updateTodo(id: number, todo: ToDoList): Observable<ToDoList> {
    return this.http.put<ToDoList>(`${this.apiUrl}/todolist/${id}`, todo);
  }

  // Delete todo
  deleteTodo(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/todolist/${id}`);
  }
}
```

### 11. **Route Guards (Optional)**

Add route guards to protect routes based on user roles:

```typescript
// auth.guard.ts
@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const requiredRole = route.data['role'];
    const user = this.authService.getStoredUser();

    if (!user) {
      this.router.navigate(['/login']);
      return false;
    }

    if (requiredRole && user.role !== requiredRole) {
      this.router.navigate(['/unauthorized']);
      return false;
    }

    return true;
  }
}

// admin.guard.ts
@Injectable({
  providedIn: 'root'
})
export class AdminGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(): boolean {
    if (!this.authService.isAdmin()) {
      this.router.navigate(['/unauthorized']);
      return false;
    }
    return true;
  }
}
```

### 12. **App Routing Updates**

Update your routing to include role-based guards:

```typescript
// app-routing.module.ts
const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { 
    path: 'todos', 
    component: TodoListComponent, 
    canActivate: [AuthGuard],
    data: { requiresAuth: true }
  },
  { 
    path: 'admin/todos', 
    component: AdminTodoComponent, 
    canActivate: [AdminGuard]
  },
  { path: 'unauthorized', component: UnauthorizedComponent },
  { path: '', redirectTo: '/todos', pathMatch: 'full' }
];
```

### 13. **Error Handling**

Add proper error handling for role-based access:

```typescript
// error.interceptor.ts
@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 403) {
          // Handle forbidden access
          this.showError('You do not have permission to access this resource');
        } else if (error.status === 401) {
          // Handle unauthorized access
          this.router.navigate(['/login']);
        }
        return throwError(error);
      })
    );
  }
}
```

## Summary of Required Changes

1. **Update interfaces** to include `personneId` and `personne` fields (allow null values)
2. **Modify registration form** to include personne selection for PERSONNE role
3. **Update auth service** to handle new registration data and role checking
4. **Modify todo service** to use new endpoints (`/my-todos`)
5. **Update todo components** to handle role-based access and automatic personne assignment
6. **Add route guards** for role-based navigation (optional)
7. **Improve error handling** for unauthorized access
8. **Update templates** to show appropriate UI based on user role
9. **Handle null personne values** throughout the application

## Testing Checklist

- [ ] Registration with PERSONNE role works
- [ ] PERSONNE users can only see their own todos
- [ ] ADMIN users can see all todos
- [ ] PERSONNE users cannot access admin-only endpoints
- [ ] Error messages are displayed for unauthorized access
- [ ] Todo creation automatically assigns the correct personne for PERSONNE users
- [ ] Role-based UI elements display correctly
- [ ] Null personne values are handled gracefully
- [ ] TypeScript compilation errors are resolved

## Quick Fix for the Specific Error

To fix the immediate TypeScript error you're seeing, update your User interface:

```typescript
// user.model.ts
export interface User {
  id: number;
  username: string;
  email: string;
  role: string;
  phoneNumber: string;
  firstname: string;
  lastname: string;
  image: string;
  personne?: Personne | null; // Allow both undefined and null
}
```

And in your login component, handle the null value explicitly:

```typescript
// login.component.ts
const userData: User = {
  id: response.user.id,
  username: response.user.username,
  email: response.user.email,
  role: response.user.role,
  firstname: response.user.firstname,
  lastname: response.user.lastname,
  phoneNumber: response.user.phoneNumber,
  image: response.user.image,
  personne: response.user.personne || null // Explicitly handle null
};
```

This integration ensures that your Angular application properly handles the new PERSONNE role functionality while maintaining a good user experience and proper security boundaries. 