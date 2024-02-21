import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TranslateModule } from '@ngx-translate/core';

import { InspectionsComponent } from './inspections.component';

describe('InspectionsComponent', () => {
  let component: InspectionsComponent;
  let fixture: ComponentFixture<InspectionsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [InspectionsComponent],
      imports: [TranslateModule.forRoot(), HttpClientModule],
    }).compileComponents();

    fixture = TestBed.createComponent(InspectionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
