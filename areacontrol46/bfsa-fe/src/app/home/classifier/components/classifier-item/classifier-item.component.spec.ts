import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { RouterTestingModule } from '@angular/router/testing';

import { ClassifierItemComponent } from './classifier-item.component';

describe('ClassifierItemComponent', () => {
  let component: ClassifierItemComponent;
  let fixture: ComponentFixture<ClassifierItemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ClassifierItemComponent],
      imports: [
        HttpClientModule,
        RouterTestingModule,
        MatSnackBarModule,
        MatDialogModule,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ClassifierItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
