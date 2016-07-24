using ALSRM.Domain.Contracts.Repositories;
using ALSRM.Domain.Models;
using ALSRM.Infrastructure.Data;
using System.Linq;
using System.Threading.Tasks;

namespace ALSRM.Infrastructure.Repositories
{
    public class PointRepository : IPointRepository
    {
        private AppDataContext _context;

        public PointRepository(AppDataContext context)
        {
            this._context = context;
        }
        public async Task<Point> CreateAsync(Point point)
        {
            Point p = _context.Points.Add(point);
            await SaveRepositoryAsync();

            return p;
        }

        public async Task<int> UpdateAsync(Point point)
        {
            _context.Entry(point).State = System.Data.Entity.EntityState.Modified;
            return await SaveRepositoryAsync();
        }

        public async Task<int> DeleteAsync(Point point)
        {
            _context.Points.Remove(point);
            return await SaveRepositoryAsync();
        }


        public async Task<Point> FindAsync(int examId, int stepNum, decimal x)
        {
            decimal [] keys = { examId, stepNum, x };

            return await _context.Points.FindAsync(keys);
        }

        public IQueryable<Point> Get()
        {
            return _context.Points;
        }

        public IQueryable<Point> Get(int examId, int stepNum)
        {
            return _context.Points.Where(p => p.ExamId == examId && p.ExamStepNum == stepNum);
        }

        public IQueryable<Point> Get(int examId, int stepNum, decimal x)
        {
            return _context.Points.Where(p => p.ExamId == examId && p.ExamStepNum == stepNum && p.X == x);
        }

        public async Task<int> SaveRepositoryAsync()
        {
           return await _context.SaveChangesAsync();
        }

        public void Dispose()
        {
            _context.Dispose();
        }

    }
}
