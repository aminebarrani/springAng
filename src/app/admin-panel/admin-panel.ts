import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-admin-panel',
  imports: [],
  templateUrl: './admin-panel.html',
  styleUrl: './admin-panel.css'
})
export class AdminPanel {
  router = inject(Router);

  goToCrud(entity: string) {
    if (entity === 'departments') {
      this.router.navigate(['/departments-crud']);
    } else if (entity === 'personne') {
      this.router.navigate(['/personne-crud']);
    } else if (entity === 'todo') {
      this.router.navigate(['/todo-crud']);
    }
  }
}
