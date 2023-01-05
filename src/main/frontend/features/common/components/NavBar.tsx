
import ProfileButton from "../../profile/components/ProfileButton";
import {useAuthTenant} from "../../authentication/hooks/useAuthTenant";

export default function NavBar() {
  const authTenant = useAuthTenant();


  /*
  <ul className="ps-3 navbar-nav me-auto">
    <li className="nav-item">
      <NavLink href="/tenant" label="Tenant" />
    </li>
  </ul>
  */

  return (
    <nav className="navbar navbar-expand-lg navbar-dark bg-dark fs-3 mb-4 custom-shadow">
      <div className="container-fluid">
        <div className="ps-3 me-auto">
          {authTenant && (<h3>{authTenant.name}</h3>)}
        </div>
        <div className="d-flex">
          <ProfileButton />
        </div>
      </div>

      <style jsx>{`
        .custom-shadow {
          border-bottom: solid #a8a8a8 1px;
        }
      `}</style>
    </nav>
  );
}
