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
	#url = this.dataset.serviceurl;
	#taskListElement;
	#taskBoxElement;
	msgElementParagraph;

	constructor() {
		super();
		// create a shadow DOM, tamplate
		const shadowRoot = this.attachShadow({ mode: "open" });
		const clone = template.content.cloneNode(true);

		//message
		this.msgElementParagraph = clone.querySelector("#message p");
		//tasklist & taskbox modal
		this.#taskListElement = clone.querySelector("task-list");
		this.#taskBoxElement = clone.querySelector("task-box");
		//append
		shadowRoot.appendChild(clone);
		//url is missing
		if (!this.#url) {
			const errroMsg = 'Url is missing';
			this.changeMessage(errroMsg);
			throw new Error(errroMsg);
		}
	}
	//lifecycle method get called when element insrted into DOM
	async connectedCallback() {
		//fetch data for the list and for statuses(both list)
		await this.#fetchStatuses();
		await this.#fetchTasks();

		//making usage of callback functions insde the taskList
		this.#taskListElement.changestatusCallback(async (id, newStatus) => {
			try {
				const response = await fetch(`${this.#url}/task/${id}`, {
					method: 'PUT',
					headers: { 'Content-Type': 'application/json' },
					body: JSON.stringify({ status: newStatus })
				});

				if (!response.ok) throw new Error('Status update failed');

				// Update the UI or notify the user
				this.changeMessage(`Status of task ${id} updated to ${newStatus}.`);
			} catch (error) {
				this.changeMessage(error.message);
				console.error('Update status error:', error);
			}

		});
		this.#taskListElement.deletetaskCallback(async (id) => {
			console.log(`${this.#url}/task/${id}`);
			try {
				console.log(`${this.#url}/task/${id}`);
				const response = await fetch(`${this.#url}/task/${id}`, {
					method: 'DELETE',
					headers: { 'Content-Type': 'application/json' }
				});
				console.log(this.#url);
				if (!response.ok) throw new Error('Deletion failed');
				this.changeMessage(`Task with ${id} got removed.`);

			} catch (error) {
				this.changeMessage(error.message);
				console.error('Delete task error:', error);
			}
		});
		//enable button
		this.enableButton();
	}

	//API methods, TODO rest API methods
	async #fetchTasks() {
		const tasks = await this.#fetchData("/tasklist", "tasks");
		if (tasks) {
			tasks.forEach((task) => {
				this.#taskListElement.showTask(task);
			})

			const message = `${tasks.length}  ${tasks.length > 1 ? 'tasks' : 'task'} found.`;
			this.changeMessage(message);
		} else {
			this.changeMessage("Failed to load tasks");
		}
	}
	async #fetchStatuses() {
		const statuses = await this.#fetchData("/allstatuses", 'allstatuses');
		this.#taskListElement.setStatuseslist(statuses);
		this.#taskBoxElement.setStatuseslist(statuses);

	}

	async #fetchData(path, key) {
		try {
			const response = await fetch(`${this.#url + path}`, {
				headers: { "Content-Type": "application/json; charset=utf-8" }
			});
			if (!response.ok) {
				const errorMsg = "Error in fetchData method";
				this.changeMessage(errorMsg);
				throw new Error(errorMsg);
			}
			const data = await response.json();
			return data[key];
		} catch (error) {
			console.error('Fetch error:', error);
			throw error;
		}
	}
	//utils
	enableButton() {
		const btn = this.shadowRoot.querySelector("#newtask button");
		btn.removeAttribute("disabled");
		//adding add event listener
		btn.addEventListener("click", () => {
			this.#taskBoxElement.show();
		});
		//adding callback to the modal
		this.#taskBoxElement.newtaskCallback = async (title, status) => {
			try {
				const response = await fetch(`${this.#url}/task`, {
					method: 'POST',
					headers: { 'Content-Type': 'application/json' },
					body: JSON.stringify({ title, status })
				});

				if (!response.ok) throw new Error('Task creation failed');

				const newTask = await response.json();
				this.#taskListElement.showTask(newTask.task);
				this.changeMessage(`New task "${title}" added.`);
				this.#taskBoxElement.close(); // Close the task box modal
			} catch (error) {
				this.changeMessage(error.message);
				console.error('New task error:', error);
			}
		};
	}

	changeMessage(msg) {
		this.msgElementParagraph.textContent = msg;
	}
}

customElements.define('task-view', TaskView);
