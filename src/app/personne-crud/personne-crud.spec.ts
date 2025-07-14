import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PersonneCrud } from './personne-crud';

describe('PersonneCrud', () => {
  let component: PersonneCrud;
  let fixture: ComponentFixture<PersonneCrud>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PersonneCrud]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PersonneCrud);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
