import { Component, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

interface Department {
  idDept: number;      // <-- changed from id_dept
  nomDept: string;
  description: string;
}

@Component({
  selector: 'app-departments-crud',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterModule],
  templateUrl: './departments-crud.html',
  styleUrls: ['./departments-crud.css']
})
export class DepartmentsCrud {
  http = inject(HttpClient);
  departments: Department[] = [];
  loading = false;
  error = '';

  newDept: Partial<Department> = { nomDept: '', description: '' };
  editDept: Department | null = null;
  editDeptCopy: Partial<Department> = {};

  selectedPersonnes: any[] = [];

  ngOnInit() {
    this.fetchDepartments();
  }

  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('auth_token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  fetchDepartments() {
    this.loading = true;
    this.error = '';
    this.http.get<Department[]>('http://localhost:8080/api/departments', {
      headers: this.getAuthHeaders()
    }).subscribe({
      next: (data) => {
        this.departments = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load departments.';
        this.loading = false;
        console.error(err);
      }
    });
  }

  addDepartment() {
    if (!this.newDept.nomDept || !this.newDept.description) return;
    this.loading = true;
    this.error = '';
    this.http.post<Department>('http://localhost:8080/api/departments', this.newDept, {
      headers: this.getAuthHeaders()
    }).subscribe({
      next: (dept) => {
        this.departments.push(dept);
        this.newDept = { nomDept: '', description: '' };
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to add department.';
        this.loading = false;
        console.error(err);
      }
    });
  }

  startEdit(dept: Department) {
    this.editDept = dept;
    this.editDeptCopy = { nomDept: dept.nomDept, description: dept.description };
  }

  saveEdit() {
    if (!this.editDept?.idDept) {
      console.error('No department selected for editing.');
      return;
    }
    this.loading = true;
    this.error = '';
    this.http.put<Department>(`http://localhost:8080/api/departments/${this.editDept.idDept}`, this.editDeptCopy, {
      headers: this.getAuthHeaders()
    }).subscribe({
      next: (updated) => {
        if (this.editDept) {
          this.editDept.nomDept = updated.nomDept;
          this.editDept.description = updated.description;
        }
        this.editDept = null;
        this.editDeptCopy = {};
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to update department.';
        this.loading = false;
        console.error(err);
      }
    });
  }

  cancelEdit() {
    this.editDept = null;
    this.editDeptCopy = {};
  }

  deleteDepartment(dept: Department) {
    console.log('Attempting to delete department:', dept);
    if (!dept?.idDept) {
      console.error('Invalid department passed for deletion:', dept);
      return;
    }
    if (!confirm(`Delete department '${dept.nomDept}'?`)) return;
    this.loading = true;
    this.error = '';
    this.http.delete(`http://localhost:8080/api/departments/${dept.idDept}`, {
      headers: this.getAuthHeaders()
    }).subscribe({
      next: () => {
        this.departments = this.departments.filter(d => d.idDept !== dept.idDept);
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to delete department.';
        this.loading = false;
        console.error(err);
      }
    });
  }

  showPersonnes(department: any) {
    // If you have all personnes loaded in this.personnes:
    this.selectedPersonnes = this.departments.filter(
      d => d.idDept === department.idDept
    );

    // If you need to fetch from backend, call your service here instead
    // this.personneService.getByDepartment(department.idDept).subscribe(personnes => {
    //   this.selectedPersonnes = personnes;
    // });
  }
}
