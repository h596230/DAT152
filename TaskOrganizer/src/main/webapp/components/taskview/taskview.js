import '../tasklist/tasklist.js';
import '../taskbox/taskbox.js';

const template = document.createElement("template");
template.innerHTML = `
    <link rel="stylesheet" type="text/css" href="${import.meta.url.match(/.*\//)[0]}/taskview.css"/>

    <h1>Tasks</h1>

    <div id="message"><p>Waiting for server data.</p></div>
    <div id="newtask"><button type="button" disabled>New task</button></div>

    <!-- The task list -->
    <task-list></task-list>
            
    <!-- The Modal -->
    <task-box></task-box>`;

/**
  * TaskView
  * The full application view
  */
class TaskView extends HTMLElement {
	#url;
	constructor() {
		super();
		// create a shadow dom
		const shadowRoot = this.attachShadow({ mode: "open" });
		//add template
		this.clone = template.content.cloneNode(true);
		//append template
		this.shadowRoot.appendChild(this.clone);
		//url
		this.#url = document.querySelector("task-view").getAttribute("data-serviceurl");
		//message id
		this.msgElement = this.shadowRoot.querySelector("#message");
		//tasklist
		this.taskList = this.shadowRoot.querySelector("task-list");
	}
	connectedCallback() {
		
		//enable button new task
		
		
		
		
		//creating taskList
		this.createTaskList();
		//change message
		this.changeMessage();
	}
	//fetchData, with defaul parameter url for reusability
	async fetchData(url, details) {
		try {
			const response = await fetch(`${url}/${details}`);
			console.log(`${url}/${details}`);
			if (!response.ok) throw new Error("Error while fetching data...");
			const data = await response.json();
			return data;
		} catch (error) {
			console.error(error);
		}
	}
	//crates task list with data from fetch
	async createTaskList() {
		try {
			let tasks = await this.fetchData(this.#url, "tasklist").then(data => data.tasks);
			let allstatuses = await this.fetchData(this.#url, "allstatuses").then(data => data.allstatuses);
			
			this.taskList.setStatuseslist(allstatuses);
			tasks.forEach((t) => this.taskList.showTask(t));
			this.changeMessage();
		} catch (error) {
			console.error("Error in createTaskList: ", error);
		}

	}
	changeMessage(){
		let n = this.taskList.getNumtasks();
		console.log(n);
		let message = `Found ${n} ${n === 1 ? "task." : "tasks."}`;
		this.msgElement.textContent = message;
	}
}

customElements.define('task-view', TaskView);
