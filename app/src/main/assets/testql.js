function clearRepositories() {
    const repositoriesContainer = document.getElementById('repositories-container');
    repositoriesContainer.innerHTML = '';
  }
  
  function displayMessage(message) {
    const messageContainer = document.getElementById('message-container');
    messageContainer.textContent = message;
  }

  function fetchRepositories(username) {
    const url = 'http://192.168.0.104:4000/graphql';
    const query = `
      query {
        getRepositories(username: "${username}") {
          name
          description
          url
        }
      }
    `;
  
    fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ query }),
    })
      .then(response => response.json())
      .then(data => {
        const repositories = data.data.getRepositories;
  
        const repositoriesContainer = document.getElementById('repositories-container');
        repositoriesContainer.innerHTML = ''; // Clear previous repositories
  
        if (repositories.length === 0) {
          displayMessage('No repositories found for the given username.');
        } else {
          displayMessage('');
  
          repositories.forEach(repository => {
            const button = document.createElement('button');
            button.textContent = repository.name;
            button.addEventListener('click', () => {
              window.open(repository.url, '_blank');
            });
            repositoriesContainer.appendChild(button);
          });
        }
      })
      .catch(error => console.log(error));
  }
  
  const form = document.getElementById('username-form');
  const input = document.getElementById('username-input');
  
  form.addEventListener('submit', event => {
    event.preventDefault();
    const username = input.value.trim();
    if (username !== '') {
      clearRepositories();
      fetchRepositories(username);
    }
  });