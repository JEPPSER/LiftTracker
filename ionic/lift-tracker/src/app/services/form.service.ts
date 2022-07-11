import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { AuthService } from "./auth.service";

@Injectable()
export class FormService {

	constructor(
		private http: HttpClient,
		private authService: AuthService
	) {}

	getFormContent(formGuid: string, filter: string = null): Observable<any> {
		let headers = new HttpHeaders();
		//headers = headers.set('Authorization', 'Bearer ' + this.authService.token);
		return this.http.get('https://colixpluginsformsapi.azurewebsites.net/api/v2/formcontent/' + formGuid + '?includes=Values' + (filter ? '&filter=' + filter : ''), { headers: headers });
	}

	postFormContent(formContent: any): Observable<any> {
		let headers = new HttpHeaders();
		//headers = headers.set('Authorization', 'Bearer ' + this.authService.token);
		return this.http.post('https://colixpluginsformsapi.azurewebsites.net/api/v2/formcontent', formContent, { headers: headers });
	}

	deleteFormContent(formContentGuid: string): Observable<any> {
		let headers = new HttpHeaders();
		headers = headers.set('Authorization', 'Bearer ' + this.authService.token);
		return this.http.delete('https://colixpluginsformsapi.azurewebsites.net/api/v2/formcontent/' + formContentGuid, { headers: headers });
	}
}
