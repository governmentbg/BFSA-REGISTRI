import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { CreateApplicationS2701Component } from './create-application-s2701.component';
import { HttpClientModule } from '@angular/common/http';

describe('CreateApplicationS2701Component', () => {
  let component: CreateApplicationS2701Component;
  let fixture: ComponentFixture<CreateApplicationS2701Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HttpClientModule],
      declarations: [CreateApplicationS2701Component],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(CreateApplicationS2701Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
