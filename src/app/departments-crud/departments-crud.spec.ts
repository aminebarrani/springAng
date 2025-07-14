import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DepartmentsCrud } from './departments-crud';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormsModule } from '@angular/forms';

describe('DepartmentsCrud', () => {
  let component: DepartmentsCrud;
  let fixture: ComponentFixture<DepartmentsCrud>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DepartmentsCrud],
      imports: [
        HttpClientTestingModule, // ✅ needed if you use HttpClient in the component
        FormsModule              // ✅ needed if you use ngModel or template forms
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DepartmentsCrud);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
