import { HttpClientModule } from '@angular/common/http';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { TranslateModule } from '@ngx-translate/core';
import { MessageResourceListComponent } from './components/message-resource-list/message-resource-list.component';

import { MessageResourceComponent } from './message-resource.component';

describe('MessageResourceComponent', () => {
  let component: MessageResourceComponent;
  let fixture: ComponentFixture<MessageResourceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MessageResourceComponent, MessageResourceListComponent],
      imports: [
        HttpClientModule,
        MatDialogModule,
        MatSnackBarModule,
        TranslateModule.forRoot(),
      ],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();

    fixture = TestBed.createComponent(MessageResourceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
