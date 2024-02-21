import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ApplicationCommonPartComponent } from './application-common-part.component';
import {  HttpClientModule } from '@angular/common/http';
import { MatDialogModule } from '@angular/material/dialog';
import { NomenclatureService } from 'src/app/services/nomenclature.service';


describe('ApplicationCommonPartComponent', () => {
  let component: ApplicationCommonPartComponent;
  let fixture: ComponentFixture<ApplicationCommonPartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ApplicationCommonPartComponent],
      imports: [HttpClientModule, MatDialogModule],
      providers: [NomenclatureService]
    }).compileComponents();

    fixture = TestBed.createComponent(ApplicationCommonPartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
