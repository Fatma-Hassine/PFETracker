import { RouterProvider } from "react-router-dom";
import { router } from "./routes";
import { RoleProvider } from "./contexts/RoleContext";

export default function App() {
  return (
    <RoleProvider>
      <RouterProvider router={router} />
    </RoleProvider>
  );
}