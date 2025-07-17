import { Component, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { Router } from '@angular/router';

interface Department {
  idDept: number | null; // allow null only for fallback
}

interface Personne {
  idPersonne: number;
  nom: string;
  prenom: string;
  adresse: string;
  sex: string;
  department: Department; 
}

@Component({
  selector: 'app-personne-crud',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './personne-crud.html',
  styleUrls: ['./personne-crud.css']
})
export class PersonneCrud {
  http = inject(HttpClient);
  personnes: Personne[] = [];
  loading = false;
  error = '';

  newPersonne: Partial<Personne> = {
    nom: '',
    prenom: '',
    adresse: '',
    sex: '',
    department: { idDept: null } // initialize with department to avoid errors
  };
  newPersonneDeptId: number | null = null;

  editPersonne: Personne | null = null;
  editPersonneCopy: Partial<Personne> = {};
  editPersonneDeptId: number | null = null;

  constructor(private route: ActivatedRoute, private router: Router) {}

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const idDept = params.get('idDept');
      if (idDept) {
        this.newPersonneDeptId = +idDept;
        this.fetchPersonnesByDepartment(+idDept);
      } else {
        this.fetchPersonnes();
      }
    });
  }

  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('auth_token');
    return new HttpHeaders({ Authorization: `Bearer ${token}` });
  }

  fetchPersonnes() {
    this.loading = true;
    this.error = '';
    this.http.get<Personne[]>('http://localhost:8080/api/personnes', {
      headers: this.getAuthHeaders()
    }).subscribe({
      next: (data) => {
        // Normalize to ensure department always exists
        this.personnes = data.map(p => ({
          ...p,
          department: p.department ?? { idDept: null }
        }));
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load personnes.';
        this.loading = false;
        console.error(err);
      }
    });
  }

  fetchPersonnesByDepartment(idDept: number) {
    this.loading = true;
    this.error = '';
    this.http.get<Personne[]>(`http://localhost:8080/api/personnes/department/${idDept}`, {
      headers: this.getAuthHeaders()
    }).subscribe({
      next: (data) => {
        this.personnes = data.map(p => ({
          ...p,
          department: p.department ?? { idDept: null }
        }));
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load personnes for this department.';
        this.loading = false;
        console.error(err);
      }
    });
  }

  addPersonne() {
    if (
      !this.newPersonne.nom ||
      !this.newPersonne.prenom ||
      !this.newPersonne.adresse ||
      !this.newPersonne.sex ||
      this.newPersonneDeptId == null
    ) {
      this.error = 'All fields are required.';
      return;
    }

    const personneToSend = {
      ...this.newPersonne,
      department: { idDept: this.newPersonneDeptId }
    };

    this.loading = true;
    this.error = '';
    this.http.post<Personne>('http://localhost:8080/api/personnes', personneToSend, {
      headers: this.getAuthHeaders()
    }).subscribe({
      next: (personne) => {
        personne.department = personne.department ?? { idDept: null };

        this.personnes.push(personne);
        this.newPersonne = { nom: '', prenom: '', adresse: '', sex: '', department: { idDept: null } };
        this.newPersonneDeptId = null;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to add personne.';
        this.loading = false;
        console.error(err);
      }
    });
  }

  startEdit(personne: Personne) {
    this.editPersonne = personne;
    this.editPersonneCopy = {
      nom: personne.nom,
      prenom: personne.prenom,
      adresse: personne.adresse,
      sex: personne.sex,
      department: { idDept: personne.department.idDept }
    };
    this.editPersonneDeptId = personne.department.idDept ?? null;
  }

  saveEdit() {
    if (!this.editPersonne?.idPersonne) {
      console.error('No personne selected for editing.');
      return;
    }

    if (
      !this.editPersonneCopy.nom ||
      !this.editPersonneCopy.prenom ||
      !this.editPersonneCopy.adresse ||
      !this.editPersonneCopy.sex ||
      this.editPersonneDeptId == null
    ) {
      this.error = 'All fields are required.';
      return;
    }

    const personneToSend = {
      ...this.editPersonneCopy,
      department: { idDept: this.editPersonneDeptId }
    };

    this.loading = true;
    this.error = '';
    this.http.put<Personne>(
      `http://localhost:8080/api/personnes/${this.editPersonne.idPersonne}`,
      personneToSend,
      { headers: this.getAuthHeaders() }
    ).subscribe({
      next: (updated) => {
        updated.department = updated.department ?? { idDept: null };

        if (this.editPersonne) {
          this.editPersonne.nom = updated.nom;
          this.editPersonne.prenom = updated.prenom;
          this.editPersonne.adresse = updated.adresse;
          this.editPersonne.sex = updated.sex;
          this.editPersonne.department = updated.department;
        }
        this.editPersonne = null;
        this.editPersonneCopy = {};
        this.editPersonneDeptId = null;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to update personne.';
        this.loading = false;
        console.error(err);
      }
    });
  }

  cancelEdit() {
    this.editPersonne = null;
    this.editPersonneCopy = {};
    this.editPersonneDeptId = null;
    this.error = '';
  }

  deletePersonne(personne: Personne) {
    if (!personne?.idPersonne) {
      console.error('Invalid personne passed for deletion:', personne);
      return;
    }

    if (!confirm(`Delete personne '${personne.nom} ${personne.prenom}'?`)) return;

    this.loading = true;
    this.error = '';
    this.http.delete(`http://localhost:8080/api/personnes/${personne.idPersonne}`, {
      headers: this.getAuthHeaders()
    }).subscribe({
      next: () => {
        this.personnes = this.personnes.filter(p => p.idPersonne !== personne.idPersonne);
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to delete personne.';
        this.loading = false;
        console.error(err);
      }
    });
  }

  showToDoList(personne: any) {
    this.router.navigate(['/todo-crud', personne.idPersonne]);
  }
}