// API Base URL
const API_BASE = '/api';

// DOM Elements
const productSearch = document.getElementById('productSearch');
const checkBtn = document.getElementById('checkBtn');
const resultContainer = document.getElementById('result');
const tunisianProductsGrid = document.getElementById('tunisianProducts');
const boycottListContainer = document.getElementById('boycottList');

// Tab Switching
document.querySelectorAll('.tab-btn').forEach(btn => {
    btn.addEventListener('click', function() {
        const targetTab = this.getAttribute('data-tab');

        // Remove active class from all tabs and panes
        document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
        document.querySelectorAll('.tab-pane').forEach(p => p.classList.remove('active'));

        // Add active class to clicked tab and corresponding pane
        this.classList.add('active');
        document.getElementById(targetTab).classList.add('active');
    });
});

// Check Product
checkBtn.addEventListener('click', checkProduct);
productSearch.addEventListener('keypress', function(e) {
    if (e.key === 'Enter') {
        checkProduct();
    }
});

async function checkProduct() {
    const productName = productSearch.value.trim();

    if (!productName) {
        alert('Veuillez entrer un nom de produit');
        return;
    }

    checkBtn.innerHTML = '<span class="loading"></span>';
    checkBtn.disabled = true;

    try {
        const response = await fetch(`${API_BASE}/check?product=${encodeURIComponent(productName)}`);
        const data = await response.json();

        displayResult(data);
    } catch (error) {
        console.error('Error:', error);
        alert('Erreur lors de la vérification du produit');
    } finally {
        checkBtn.innerHTML = 'Vérifier';
        checkBtn.disabled = false;
    }
}

function displayResult(data) {
    resultContainer.classList.remove('result-boycotted', 'result-safe');

    if (data.boycotted) {
        resultContainer.classList.add('result-boycotted');
        resultContainer.innerHTML = `
            <h3>${data.message}</h3>
            <span class="boycott-badge badge-${data.boycottLevel.toLowerCase()}">
                Niveau: ${data.boycottLevel}
            </span>
            <p class="boycott-reason"><strong>Raison:</strong> ${data.reason}</p>
            ${data.alternatives && data.alternatives.length > 0 ? `
                <h4 style="margin-top: 20px;">✅ Alternatives Tunisiennes:</h4>
                <div class="products-grid" style="margin-top: 15px;">
                    ${data.alternatives.map(product => createProductCard(product)).join('')}
                </div>
            ` : ''}
        `;
    } else {
        resultContainer.classList.add('result-safe');
        resultContainer.innerHTML = `
            <h3>${data.message}</h3>
            <p>Ce produit peut être consommé en toute tranquillité.</p>
        `;
    }

    resultContainer.classList.add('show');
}

// Load Tunisian Products
async function loadTunisianProducts() {
    try {
        const response = await fetch(`${API_BASE}/tunisian-products`);
        const products = await response.json();

        tunisianProductsGrid.innerHTML = products.map(product => createProductCard(product)).join('');
    } catch (error) {
        console.error('Error loading products:', error);
        tunisianProductsGrid.innerHTML = '<p>Erreur lors du chargement des produits</p>';
    }
}

function createProductCard(product) {
    return `
        <div class="product-card">
            <span class="tunisian-flag">🇹🇳</span>
            <h3>${product.name}</h3>
            <p class="product-brand">${product.brand}</p>
            <span class="product-category">${product.category}</span>
            <p class="product-description">${product.description || ''}</p>
        </div>
    `;
}

// Load Boycott List
async function loadBoycottList() {
    try {
        const response = await fetch(`${API_BASE}/boycott-list`);
        const boycottProducts = await response.json();

        boycottListContainer.innerHTML = boycottProducts.map(product => `
            <div class="boycott-item">
                <h3>${product.name}</h3>
                <p class="product-brand">Marque: ${product.brand}</p>
                <span class="boycott-badge badge-${product.boycottLevel.toLowerCase()}">
                    ${product.boycottLevel}
                </span>
                <p class="boycott-reason">${product.reason}</p>
            </div>
        `).join('');
    } catch (error) {
        console.error('Error loading boycott list:', error);
        boycottListContainer.innerHTML = '<p>Erreur lors du chargement de la liste</p>';
    }
}

// Initialize
document.addEventListener('DOMContentLoaded', function() {
    loadTunisianProducts();
    loadBoycottList();
});