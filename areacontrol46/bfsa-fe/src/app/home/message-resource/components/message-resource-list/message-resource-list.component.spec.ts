import { HttpClientModule } from '@angular/common/http';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';

import { MessageResourceListComponent } from './message-resource-list.component';

describe('MessageResourceListComponent', () => {
  let component: MessageResourceListComponent;
  let fixture: ComponentFixture<MessageResourceListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MessageResourceListComponent],
      imports: [HttpClientModule, MatDialogModule, MatSnackBarModule],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();

    fixture = TestBed.createComponent(MessageResourceListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
