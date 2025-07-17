import { Routes } from '@angular/router';
import { Login } from './login/login';
import { Welcome } from './welcome/welcome';
import { Register } from './register/register';
import { AdminPanel } from './admin-panel/admin-panel';
import { DepartmentsCrud } from './departments-crud/departments-crud';
import { TodoCrud } from './todo-crud/todo-crud';
import { PersonneCrud } from './personne-crud/personne-crud'; // adjust path if needed
import { Dashboard } from './dashboard/dashboard';

export const routes: Routes = [
  { path: '', component: Login },
  { path: 'dashboard', component: Dashboard },
  { path: 'welcome', component: Welcome },
  { path: 'register', component: Register },
  { path: 'admin-panel', component: AdminPanel },
  { path: 'departments-crud', component: DepartmentsCrud },
  { path: 'personne-crud', component: PersonneCrud },
  { path: 'todo-crud', component: TodoCrud },
  { path: 'todo-crud/:personneId', component: TodoCrud },
  {
    path: 'personnes/department/:idDept',
    component: PersonneCrud
  }
];
