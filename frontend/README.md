# StockPulse Frontend

React dashboard for the StockPulse AI Inventory & Dynamic Pricing Advisor.

## 🚀 Development

### Install Dependencies
```bash
npm install
```

### Run Development Server
```bash
npm run dev
```

The frontend will start on `http://localhost:5173` and proxy API requests to the backend at `http://localhost:8080`.

## 📁 Project Structure

```
src/
├── components/     # Reusable UI components
├── services/       # API service functions
├── types/          # TypeScript type definitions
├── App.tsx         # Main application component
├── main.tsx        # Application entry point
├── App.css         # Global styles
└── index.css       # Base styles
```

## 🎯 Features

- Real-time dashboard with polling
- Product listing with key metrics
- Interactive product simulation
- Pricing and reorder suggestion management
- Responsive design
- Dark mode support

## 🛠️ Technologies

- **React 18** with TypeScript
- **Vite** for fast development
- **Fetch API** for backend communication
- **CSS Modules** for styling

## 🔄 API Integration

The frontend communicates with the backend via REST APIs:

- `/products` - Product management
- `/pricing-suggestions` - Pricing recommendations
- `/reorder-suggestions` - Reorder recommendations

All APIs are proxied through Vite to avoid CORS issues during development.