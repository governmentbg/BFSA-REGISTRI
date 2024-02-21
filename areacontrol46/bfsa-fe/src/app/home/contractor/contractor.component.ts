import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-applicant',
  templateUrl: './contractor.component.html',
  styleUrls: ['./contractor.component.scss'],
})
export class ContractorComponent implements OnInit {
  constructor(private title: Title) {}

  ngOnInit(): void {
    this.title.setTitle('Contractors');
  }
}
