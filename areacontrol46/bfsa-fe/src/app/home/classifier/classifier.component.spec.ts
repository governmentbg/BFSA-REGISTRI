import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TranslateModule } from '@ngx-translate/core';

import { ClassifierComponent } from './classifier.component';

describe('ClassifierComponent', () => {
  let component: ClassifierComponent;
  let fixture: ComponentFixture<ClassifierComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ClassifierComponent],
      imports: [TranslateModule.forRoot()],
    }).compileComponents();

    fixture = TestBed.createComponent(ClassifierComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
